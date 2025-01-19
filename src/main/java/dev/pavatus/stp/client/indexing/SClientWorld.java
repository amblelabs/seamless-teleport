package dev.pavatus.stp.client.indexing;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public interface SClientWorld {
    void stp$setIndex(int index);
    int stp$index();

    void stp$setMain();
    void stp$unsetMain();

    void stp$init();

    ClientPlayNetworkHandler stp$networkHandler();
    ClientPlayerEntity stp$player();
}
