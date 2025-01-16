package dev.pavatus.stp.mixin;

import dev.pavatus.stp.STPChunk;
import dev.pavatus.stp.STPMod;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

@Mixin(WorldChunk.class)
public class WorldChunkMixin implements STPChunk {

    @Shadow @Final
    World world;

    @Unique
    private Set<WeakReference<ServerWorld>> linkedTo;

    @Override
    public Set<WeakReference<ServerWorld>> stp$linkedTo() {
        if (this.world.isClient()) {
            STPMod.LOGGER.error("Tried to get linked chunks in client logic!", new Throwable());
            return null;
        }

        if (this.linkedTo == null)
            this.linkedTo = new HashSet<>();

        return linkedTo;
    }
}
