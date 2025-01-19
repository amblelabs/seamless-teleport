package dev.pavatus.stp.client.indexing;

import net.minecraft.client.network.ClientPlayNetworkHandler;

public interface SClientWorld {
    void stp$setIndex(int index);
    int stp$index();

    void stp$setMain();
    void stp$unsetMain();

    void stp$init();

    ClientPlayNetworkHandler stp$networkHandler();
}
