package dev.pavatus.stp;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PortalBlock {

    RegistryKey<World> destWorld();
    BlockPos destPos();
}
