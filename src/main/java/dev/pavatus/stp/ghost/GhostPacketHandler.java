package dev.pavatus.stp.ghost;

import dev.pavatus.stp.indexing.IndexableWorld;
import dev.pavatus.stp.util.WorldUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.util.Identifier;

import java.util.List;

public class GhostPacketHandler {

    public static final Identifier PACKET_ID = new Identifier("stp", "packet");
    public static final Identifier PACKET_BUNDLE_ID = new Identifier("stp", "packet_bundle");

    public static void send(GhostPlayerEntity ghost, Packet<?> packet) {
        if (packet instanceof BundleS2CPacket bundle) {
            sendBundle(ghost, bundle);
            return;
        }

        sendSimple(ghost, packet);
    }

    private static PacketByteBuf prepareBuffer(GhostPlayerEntity ghost) {
        PacketByteBuf buf = PacketByteBufs.create();
        int worldIndex = WorldUtil.getWorldIndex(ghost.getServerWorld());

        buf.writeVarInt(worldIndex);
        return buf;
    }

    private static void writePacket(PacketByteBuf buf, Packet<?> packet) {
        buf.writeVarInt(NetworkState.PLAY.getPacketId(NetworkSide.CLIENTBOUND, packet));
        packet.write(buf);
    }

    private static void sendSimple(GhostPlayerEntity ghost, Packet<?> packet) {
        PacketByteBuf buf = prepareBuffer(ghost);
        writePacket(buf, packet);

        ServerPlayNetworking.send(ghost, PACKET_ID, buf);
    }

    private static void sendBundle(GhostPlayerEntity ghost, BundleS2CPacket bundle) {
        PacketByteBuf buf = prepareBuffer(ghost);

        List<Packet<ClientPlayPacketListener>> packets =
                ((List<Packet<ClientPlayPacketListener>>) bundle.getPackets());

        buf.writeVarInt(packets.size());

        for (Packet<?> packet : packets) {
            writePacket(buf, packet);
        }

        ServerPlayNetworking.send(ghost, PACKET_BUNDLE_ID, buf);
    }
}
