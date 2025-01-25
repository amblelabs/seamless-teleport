package dev.pavatus.stp.interworld;

import dev.pavatus.stp.STPMod;
import dev.pavatus.stp.ghost.GhostServerPlayerEntity;
import dev.pavatus.stp.ghost.SServerPlayerEntity;
import dev.pavatus.stp.indexing.SServerWorld;
import dev.pavatus.stp.indexing.ServerWorldIndexer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class InterWorldPacketHandler {

    public static final Identifier PLAY_PACKET = new Identifier(STPMod.MOD_ID, "play_packet");
    public static final Identifier PLAY_BUNDLE_PACKET = new Identifier(STPMod.MOD_ID, "play_bundle_packet");
    public static final Identifier LOAD_PACKET = new Identifier(STPMod.MOD_ID, "load");

    public static void init() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerWorld world = handler.getPlayer().getServerWorld();
            Collection<GhostServerPlayerEntity> ghosts = ((SServerPlayerEntity) handler.getPlayer()).stp$ghosts();

            for (ServerPlayerEntity ghost : ghosts) {
                world.removePlayer(ghost, Entity.RemovalReason.DISCARDED);
            }

            ghosts.clear();
        });
    }

    public static boolean shouldProcessPacket(Packet<?> packet) {
        return !(packet instanceof CustomPayloadS2CPacket);
    }

    public static void sendPlayPacket(ServerPlayerEntity player, Packet<?> packet, PacketCallbacks callbacks) {
        if (packet instanceof BundleS2CPacket bundle) {
            sendBundlePacket(player, bundle, callbacks);
            return;
        }

        handlePacket(player, callbacks,
                buf -> writePacket(packet, buf));
    }

    private static void sendBundlePacket(ServerPlayerEntity player, BundleS2CPacket bundle, PacketCallbacks callbacks) {
        handlePacket(player, callbacks, buf -> {
            List<Packet<ClientPlayPacketListener>> packets = (List<Packet<ClientPlayPacketListener>>) bundle.getPackets();

            buf.writeVarInt(packets.size());

            for (Packet<?> packet : packets) {
                writePacket(packet, buf);
            }
        });
    }

    private static void handlePacket(ServerPlayerEntity player, PacketCallbacks callbacks, Consumer<PacketByteBuf> consumer) {
        PacketByteBuf buf = PacketByteBufs.create();

        consumer.accept(buf);
        writeWorld(player.getServerWorld(), buf);

        player.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(PLAY_PACKET, buf), callbacks);
    }

    private static void writeWorld(ServerWorld world, PacketByteBuf buf) {
        buf.writeInt(0xCAFEBABE);

        int index = ServerWorldIndexer.getWorldIndex(world);
        buf.writeVarInt(index);
    }

    private static void writePacket(Packet<?> packet, PacketByteBuf buf) {
        int packetId = NetworkState.PLAY.getPacketId(NetworkSide.CLIENTBOUND, packet);

        if (packetId == -1) {
            STPMod.LOGGER.error("Failed to serialize an unregistered packet {}", packet);
            return;
        }

        buf.writeVarInt(packetId);
        packet.write(buf);
    }

    public static GhostServerPlayerEntity createPacketWatcher(ServerPlayerEntity player, ServerWorld targetWorld, BlockPos pos) {
        GhostServerPlayerEntity ghost = new GhostServerPlayerEntity(player, targetWorld, pos);

        targetWorld.getServer().getPlayerManager().sendToAll(PlayerListS2CPacket.entryFromPlayer(List.of(ghost)));
        targetWorld.spawnEntity(ghost);

        ghost.interactionManager.changeGameMode(GameMode.SPECTATOR);

        ((SServerPlayerEntity) player).stp$ghosts().add(ghost);
        return ghost;
    }

    public static void loadWorld(ServerPlayerEntity player, ServerWorld world) {
        PacketByteBuf buf = PacketByteBufs.create();

        // TODO: implement index -> registry key for indexing
        buf.writeVarInt(((SServerWorld) world).stp$index());
        buf.writeRegistryKey(world.getRegistryKey());
        buf.writeRegistryKey(world.getDimensionKey());
        buf.writeLong(world.getSeed());
        buf.writeBoolean(world.isDebugWorld());
        buf.writeBoolean(world.isFlat());

        ServerPlayNetworking.send(player, LOAD_PACKET, buf);
    }
}
