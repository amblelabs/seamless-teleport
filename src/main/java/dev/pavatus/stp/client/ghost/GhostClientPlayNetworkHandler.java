package dev.pavatus.stp.client.ghost;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.network.ClientConnection;
import org.jetbrains.annotations.Nullable;

public class GhostClientPlayNetworkHandler extends ClientPlayNetworkHandler {

    public GhostClientPlayNetworkHandler(Screen screen, ClientConnection connection, @Nullable ServerInfo serverInfo, GameProfile profile, WorldSession worldSession) {
        super(MinecraftClient.getInstance(), screen, connection, serverInfo, profile, worldSession);
    }

    public static GhostClientPlayNetworkHandler create(ClientPlayerEntity player) {
        return new GhostClientPlayNetworkHandler(null, player.networkHandler.getConnection(), player.networkHandler.getServerInfo(), player.getGameProfile(), null);
    }
}
