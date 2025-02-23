package dev.pavatus.stp.ghost;

import dev.pavatus.stp.interworld.InterWorldPacketHandler;
import dev.pavatus.stp.mixin.ghost.EntityPacketIdAccessor;
import dev.pavatus.stp.mixin.ghost.EntityPacketEntityAccessor;
import dev.pavatus.stp.mixin.ghost.ServerPlayNetworkHandlerAccessor;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class GhostServerPlayNetworkHandler extends ServerPlayNetworkHandler {

    public GhostServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, GhostServerPlayerEntity player) {
        super(server, connection, player);

        // handle C2S packets via the original network handler
        connection.setPacketListener(player.getOwner().networkHandler);
    }

    @Override
    public void tick() { }

    @Override
    public void sendPacket(Packet<?> packet, @Nullable PacketCallbacks callbacks) {
        if (!(this.player instanceof GhostServerPlayerEntity ghost))
            return;

        if (!this.filterPacket(packet))
            return;

        if (!(packet instanceof CustomPayloadS2CPacket)) {
            InterWorldPacketHandler.sendPlayPacket(ghost, packet, callbacks);
            return;
        }

        super.sendPacket(packet, callbacks);
    }

    private boolean filterPacket(Packet<?> packet) {
        if (packet instanceof CustomPayloadS2CPacket)
            return true;

        if (packet instanceof ChunkDataS2CPacket)
            return false;

        if (packet instanceof ChunkDeltaUpdateS2CPacket)
            return false;

        if (packet instanceof LightUpdateS2CPacket)
            return false;

        if (packet instanceof EntityStatusS2CPacket)
            return true;

        if (packet instanceof EntitiesDestroyS2CPacket)
            return true;

        if (packet instanceof EntitySpawnS2CPacket)
            return true;

        if (packet instanceof EntityAttributesS2CPacket)
            return true;

        if (packet instanceof EntityTrackerUpdateS2CPacket)
            return true;

        if (packet instanceof ChunkRenderDistanceCenterS2CPacket)
            return false;

        if (packet instanceof BlockEntityUpdateS2CPacket)
            return true;

        if (packet instanceof BundleS2CPacket bundle) {
            for (Packet<?> bundled : bundle.getPackets()) {
                if (!filterPacket(bundled))
                    return false;
            }

            return true;
        }

        ServerPlayerEntity owner = ((GhostServerPlayerEntity) this.player).getOwner();
        ServerPlayerEntity self = this.player;

        if (packet instanceof EntityVelocityUpdateS2CPacket p)
            return p.getId() != owner.getId() && p.getId() != self.getId();

        if (packet instanceof EntityS2CPacket p)
            return ((EntityPacketIdAccessor) p).getId() != owner.getId()
                    && ((EntityPacketIdAccessor) p).getId() != self.getId();

        if (packet instanceof EntityPositionS2CPacket p)
            return p.getId() != owner.getId() && p.getId() != self.getId();

        if (packet instanceof EntitySetHeadYawS2CPacket p)
            return ((EntityPacketEntityAccessor) p).getEntity() != owner.getId()
                    && ((EntityPacketEntityAccessor) p).getEntity() != self.getId();


        System.out.println("unhandled packet: " + packet);

        return true;

        /*int id = -1;
        if (packet instanceof EntityS2CPacket p)
            id = ((EntityPacketAccessor) p).getId();

        if (packet instanceof EntityPositionS2CPacket p)
            id = p.getId();

        if (packet instanceof EntityVelocityUpdateS2CPacket p)
            id = p.getId();

        if (id == -1)
            return true;

        return owner.getId() != id && self.getId() != id;*/
    }



    public static GhostServerPlayNetworkHandler create(GhostServerPlayerEntity ghost) {
        MinecraftServer server = ghost.getServer();
        ClientConnection connection = ((ServerPlayNetworkHandlerAccessor) ghost.getOwner().networkHandler).getConnection();

        return new GhostServerPlayNetworkHandler(server, connection, ghost);
    }
}
