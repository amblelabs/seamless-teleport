package dev.pavatus.stp.mixin.indexing;

import dev.pavatus.stp.indexing.IndexableWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(World.class)
public class WorldIndexingMixin implements IndexableWorld {

    @Unique
    private int index = -1;

    @Override
    public int stp$getIndex() {
        return index;
    }

    @Override
    public void stp$setIndex(int index) {
        this.index = index;
    }
}
