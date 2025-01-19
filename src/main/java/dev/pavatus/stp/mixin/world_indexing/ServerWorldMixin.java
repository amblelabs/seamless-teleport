package dev.pavatus.stp.mixin.world_indexing;

import dev.pavatus.stp.indexing.SServerWorld;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements SServerWorld {

    @Unique
    private int index = -1;

    @Override
    public void stp$setIndex(int index) {
        this.index = index;
    }

    @Override
    public int stp$index() {
        return index;
    }
}
