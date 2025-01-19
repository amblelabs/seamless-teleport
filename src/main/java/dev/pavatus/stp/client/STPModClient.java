package dev.pavatus.stp.client;

import dev.pavatus.stp.client.ghost.ClientGhostPlayerManager;
import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class STPModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientWorldIndexer.init();
        ClientGhostPlayerManager.init();
    }
}
