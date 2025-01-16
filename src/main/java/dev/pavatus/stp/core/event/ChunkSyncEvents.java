package dev.pavatus.stp.core.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkSyncEvents {

    public static final Event<SyncEvent> SYNC = EventFactory.createArrayBacked(SyncEvent.class, callbacks -> (world, player, chunk) -> {
        for (SyncEvent callback : callbacks) {
            callback.onSync(world, player, chunk);
        }
    });

    public static final Event<DesyncEvent> DESYNC = EventFactory.createArrayBacked(DesyncEvent.class, callbacks -> (world, player, chunk) -> {
        for (DesyncEvent callback : callbacks) {
            callback.onDesync(world, player, chunk);
        }
    });

    public interface SyncEvent {
        void onSync(ServerWorld world, ServerPlayerEntity player, WorldChunk chunk);
    }

    public interface DesyncEvent {
        void onDesync(ServerWorld world, ServerPlayerEntity player, ChunkPos chunk);
    }
}
