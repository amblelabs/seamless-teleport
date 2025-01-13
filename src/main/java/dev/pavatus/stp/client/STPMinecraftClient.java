package dev.pavatus.stp.client;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;

public interface STPMinecraftClient {
    void stp$joinWorld(ClientWorld world);

    void stp$resetWorldRenderer();
    void stp$setWorldRenderer(WorldRenderer renderer);
}
