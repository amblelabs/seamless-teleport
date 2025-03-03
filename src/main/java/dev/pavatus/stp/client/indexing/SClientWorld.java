package dev.pavatus.stp.client.indexing;

import dev.pavatus.stp.client.ghost.GhostClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;

public interface SClientWorld {
    void stp$setIndex(int index);
    int stp$index();
    GhostClientPlayerEntity stp$getGhostPlayer();

    void stp$setMain();
    void stp$unsetMain();

    void stp$init();

    ClientPlayNetworkHandler stp$networkHandler();
    ClientPlayerEntity stp$player();
}
