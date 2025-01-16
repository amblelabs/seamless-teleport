package dev.pavatus.stp.mixin;

import com.google.common.collect.ImmutableList;
import dev.pavatus.stp.ChunkSyncEvents;
import dev.pavatus.stp.STPChunkStorage;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = ThreadedAnvilChunkStorage.class)
public class ThreadedAnvilChunkStorageMixin implements STPChunkStorage {

    @Shadow @Final
    ServerWorld world;

    @Unique
    private final ReferenceSet<ServerPlayerEntity> watching = new ReferenceOpenHashSet<>();

    @Inject(method = "sendChunkDataPackets", at = @At("TAIL"))
    public void sendChunkDataPackets(ServerPlayerEntity player, MutableObject<ChunkDataS2CPacket> cachedDataPacket, WorldChunk chunk, CallbackInfo ci) {
        ChunkSyncEvents.SYNC.invoker().onSync(this.world, player, chunk);
    }

    @Redirect(method = "sendWatchPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendUnloadChunkPacket(Lnet/minecraft/util/math/ChunkPos;)V"))
    public void sendUnloadChunkPacket(ServerPlayerEntity instance, ChunkPos chunkPos) {
        ChunkSyncEvents.DESYNC.invoker().onDesync(this.world, instance, chunkPos);
        instance.sendUnloadChunkPacket(chunkPos);
    }

    @Redirect(method = "sendWatchPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getWorld()Lnet/minecraft/world/World;"))
    public World sendWatchPackets(ServerPlayerEntity instance) {
        if (this.watching.contains(instance))
            return this.world;

        return instance.getWorld();
    }

    @Redirect(method = "getPlayersWatchingChunk(Lnet/minecraft/util/math/ChunkPos;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;build()Lcom/google/common/collect/ImmutableList;"))
    public ImmutableList<ServerPlayerEntity> getPlayersWatchingChunk(ImmutableList.Builder<ServerPlayerEntity> instance) {
        for (ServerPlayerEntity watching : this.watching) {
            instance.add(watching);
        }

        return instance.build();
    }

    @Redirect(method = "getPlayersWatchingChunk(Lnet/minecraft/util/math/ChunkPos;Z)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;build()Lcom/google/common/collect/ImmutableList;"))
    public ImmutableList<ServerPlayerEntity> getPlayersWatchingChunk2(ImmutableList.Builder<ServerPlayerEntity> instance) {
        for (ServerPlayerEntity watching : this.watching) {
            instance.add(watching);
        }

        return instance.build();
    }

    @Override
    public ReferenceSet<ServerPlayerEntity> stp$watching() {
        return watching;
    }
}
