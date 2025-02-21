package dev.pavatus.stp.util;

import dev.pavatus.stp.mixin.access.ConnectionAccess;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class NetworkUtil {

    public static ClientConnection getConnection(ServerPlayNetworkHandler handler) {
        return ((ConnectionAccess) handler).getConnection();
    }
}
