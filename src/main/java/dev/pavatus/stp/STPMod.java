package dev.pavatus.stp;

import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

public class STPMod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("stp");

    public static final Identifier TP = new Identifier("stp", "tp");

    public static void moveToWorld(ServerPlayerEntity player, ServerWorld destination) {
        NastyNetworking.moveToWorld(player, destination);
    }

    /* (server)
    teleport -> player respawn packet
            |-> send world info -> player spawn position packet

       (client)
    player respawn packet -> player & world stuff -> set loading screen
    player spawn position packet -> remove loading screen
     */
    public static void teleport(ServerPlayerEntity player, ServerWorld targetWorld, Vec3d pos, float yaw, float pitch) {
        NastyNetworking.handlePlayerTp(player, targetWorld, pos, yaw, pitch);
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            STPCommand.register(dispatcher);
        });

        ChunkSyncEvents.SYNC.register((world, player, chunk) -> {
            for (WeakReference<ServerWorld> linkedWorld : ((STPChunk) chunk).stp$linkedTo()) {
                STPChunkStorage accessor = ((STPChunkStorage) linkedWorld.get()
                        .getChunkManager().threadedAnvilChunkStorage);

                accessor.stp$watching().add(player);
            }
        });

        ChunkSyncEvents.DESYNC.register((world, player, chunk) -> {
            for (WeakReference<ServerWorld> linkedWorld : ((STPChunk) chunk).stp$linkedTo()) {
                STPChunkStorage accessor = ((STPChunkStorage) linkedWorld.get()
                        .getChunkManager().threadedAnvilChunkStorage);

                accessor.stp$watching().remove(player);
            }
        });
    }
}
