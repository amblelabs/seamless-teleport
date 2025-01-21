package dev.pavatus.stp.ghost;

import dev.pavatus.stp.STPMod;
import dev.pavatus.stp.indexing.ServerWorldIndexer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.*;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class GhostPlayerManager {

    public static final Identifier PLAY_PACKET = new Identifier(STPMod.MOD_ID, "play_packet");

    public static void init() {

    }

    public static void sendPlayPacket(GhostServerPlayerEntity player, Packet<?> packet, PacketCallbacks callbacks) {
        int packetId = NetworkState.PLAY.getPacketId(NetworkSide.CLIENTBOUND, packet);

        if (packetId == -1) {
            STPMod.LOGGER.error("Failed to serialize an unregistered packet {}", packet);
            return;
        }

        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeVarInt(packetId);
        packet.write(buf);

        buf.writeInt(0xCAFEBABE);

        int index = ServerWorldIndexer.getWorldIndex(player.getServerWorld());
        buf.writeVarInt(index);

        player.networkHandler.sendPacket(ServerPlayNetworking.createS2CPacket(PLAY_PACKET, buf), callbacks);
    }

    public static GhostServerPlayerEntity create(ServerPlayerEntity player, ServerWorld targetWorld, BlockPos pos) {
        GhostServerPlayerEntity ghost = new GhostServerPlayerEntity(player, targetWorld, pos);

        targetWorld.getPlayers().add(ghost);
        targetWorld.spawnEntity(ghost);

        return ghost;
    }
}
