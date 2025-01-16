package dev.pavatus.stp.v2;

import dev.pavatus.stp.mixin.v2.ServerPlayNetworkHandlerAccessor;
import net.fabricmc.fabric.impl.event.interaction.FakePlayerNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class STPServerPlayNetworkHandler extends ServerPlayNetworkHandler {

    private final STPServerPlayerEntity stpPlayer;

    public STPServerPlayNetworkHandler(MinecraftServer server, ServerPlayNetworkHandler networkHandler, GhostServerPlayerEntity player) {
        super(server, new STPClientConnection(networkHandler), player);

        this.stpPlayer = (STPServerPlayerEntity) player;
    }

    private GhostServerPlayerEntity ghost() {
        return (GhostServerPlayerEntity) this.player;
    }
}
