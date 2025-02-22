package dev.pavatus.stp.mixin;

import dev.pavatus.stp.PortalBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetherPortalBlock implements PortalBlock {

    @Override
    public RegistryKey<World> destWorld() {
        return null;
    }

    @Override
    public BlockPos destPos() {
        return null;
    }
}
