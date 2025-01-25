package dev.pavatus.stp.client.interworld;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public interface SMinecraftClient {
    ClientPlayerEntity stp$player();
    ClientWorld stp$world();

    default void stp$update() {
        this.stp$updatePlayer();
        this.stp$updateWorld();
    }

    void stp$updatePlayer();
    void stp$updateWorld();
}
