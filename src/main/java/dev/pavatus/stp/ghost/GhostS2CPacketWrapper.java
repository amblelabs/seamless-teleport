package dev.pavatus.stp.ghost;

import dev.pavatus.stp.indexing.ServerWorldIndexer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

public class GhostS2CPacketWrapper<T extends PacketListener> implements Packet<T> {

    private static final int MAGIC = 0xCAFEBABE;

    private final GhostServerPlayerEntity ghost;
    private final Packet<T> packet;

    public GhostS2CPacketWrapper(GhostServerPlayerEntity ghost, Packet<T> packet) {
        this.ghost = ghost;
        this.packet = packet;
    }

    @Override
    public void write(PacketByteBuf buf) {
        this.packet.write(buf);

        buf.writeInt(MAGIC);

        int index = ServerWorldIndexer.getWorldIndex(ghost.getServerWorld());
        buf.writeVarInt(index);
    }

    @Override
    public void apply(PacketListener listener) { }

    public Packet<T> getPacket() {
        return packet;
    }
}
