package dev.pavatus.stp.client.indexing;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;

public interface SMinecraftClient {
    List<SClientWorld> stp$worlds();
    Integer stp$getWorldIndex(RegistryKey<World> key);
    void stp$setWorldIndex(RegistryKey<World> key, int index);
}
