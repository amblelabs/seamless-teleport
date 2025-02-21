package dev.pavatus.stp.ghost;

import dev.pavatus.stp.util.NetworkUtil;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class GhostPlayNetworkHandler extends ServerPlayNetworkHandler {

    private final GhostPlayerEntity ghost;
    private final ServerPlayerEntity owner;

    public GhostPlayNetworkHandler(GhostPlayerEntity ghost) {
        this(ghost, ghost.owner());
    }

    private GhostPlayNetworkHandler(GhostPlayerEntity ghost, ServerPlayerEntity owner) {
        this(owner.networkHandler, ghost, owner);
    }

    private GhostPlayNetworkHandler(ServerPlayNetworkHandler parent, GhostPlayerEntity ghost, ServerPlayerEntity owner) {
        super(ghost.server, NetworkUtil.getConnection(parent), ghost);

        this.ghost = ghost;
        this.owner = owner;

        this.fixupListener();
    }

    private void fixupListener() {
        NetworkUtil.getConnection(this).setPacketListener(owner.networkHandler);
    }

    @Override
    public void sendPacket(Packet<?> packet, @Nullable PacketCallbacks callbacks) {
        if (packet instanceof CustomPayloadS2CPacket) {
            super.sendPacket(packet, callbacks);
            return;
        }

        GhostPacketHandler.send(this.ghost, packet);
    }
}
