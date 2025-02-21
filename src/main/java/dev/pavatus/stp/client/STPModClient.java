package dev.pavatus.stp.client;

import dev.pavatus.stp.client.ghost.ClientGhostPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

@Environment(EnvType.CLIENT)
public class STPModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientGhostPacketHandler.init();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

        });
    }
}
