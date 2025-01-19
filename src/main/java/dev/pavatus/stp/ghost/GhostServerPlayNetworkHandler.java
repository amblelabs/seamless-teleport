package dev.pavatus.stp.ghost;

import dev.pavatus.stp.mixin.ghost.ServerPlayNetworkHandlerAccessor;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class GhostServerPlayNetworkHandler extends ServerPlayNetworkHandler {

    public GhostServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
        super(server, connection, player);
    }

    @Override
    public void sendPacket(Packet<?> packet, @Nullable PacketCallbacks callbacks) {
        if (!(this.player instanceof GhostServerPlayerEntity ghost))
            return;

        if (!(packet instanceof CustomPayloadS2CPacket)) {
            GhostPlayerManager.sendPlayPacket(ghost, packet, callbacks);
            return;
        }

        super.sendPacket(packet, callbacks);
    }

    public static GhostServerPlayNetworkHandler create(GhostServerPlayerEntity ghost) {
        MinecraftServer server = ghost.getServer();
        ClientConnection connection = ((ServerPlayNetworkHandlerAccessor) ghost.getOwner().networkHandler).getConnection();

        return new GhostServerPlayNetworkHandler(server, connection, ghost);
    }
}
