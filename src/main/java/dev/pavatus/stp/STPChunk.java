package dev.pavatus.stp;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Set;

public interface STPChunk {
    Set<RegistryKey<World>> stp$linkedTo();
}
