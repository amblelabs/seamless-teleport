package dev.pavatus.stp.client.indexing;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;

public interface SMinecraftClient {
    List<ClientWorld> stp$worlds();
    Integer stp$getWorldIndex(RegistryKey<World> key);
    void stp$setWorldIndex(RegistryKey<World> key, int index);
}
