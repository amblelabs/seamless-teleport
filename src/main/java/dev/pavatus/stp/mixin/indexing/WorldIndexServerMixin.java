package dev.pavatus.stp.mixin.indexing;

import dev.pavatus.stp.indexing.IndexableWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class WorldIndexServerMixin implements IndexableWorld.Holder {

    @Shadow public abstract Iterable<ServerWorld> getWorlds();

    @Unique
    private List<IndexableWorld> indexableWorlds;

    @Override
    public List<IndexableWorld> stp$worlds() {
        if (indexableWorlds == null)
            this.stp$refresh();

        return indexableWorlds;
    }

    @Override
    public void stp$refresh() {
        this.indexableWorlds = new ArrayList<>();
        int index = 0;

        for (ServerWorld world : this.getWorlds()) {
            if (!(world instanceof IndexableWorld indexable))
                continue;

            indexable.stp$setIndex(index);
            this.indexableWorlds.add(indexable);

            index++;
        }
    }

    @Override
    public void stp$add(IndexableWorld world) {
        if (world.stp$getIndex() == -1)
            world.stp$setIndex(this.indexableWorlds.size());

        this.indexableWorlds.add(world);
    }

    @Override
    public void stp$remove(IndexableWorld world) {
        if (world.stp$getIndex() == -1)
            return;

        this.indexableWorlds.set(world.stp$getIndex(), null);
    }

    @Override
    public IndexableWorld stp$get(int index) {
        return this.indexableWorlds.get(index);
    }

    @Override
    public int stp$getWorldIndex(IndexableWorld world) {
        return world.stp$getIndex();
    }
}
