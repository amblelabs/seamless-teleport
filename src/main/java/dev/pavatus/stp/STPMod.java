package dev.pavatus.stp;

import dev.pavatus.stp.command.STPDebugCommand;
import dev.pavatus.stp.event.ChunkSyncEvents;
import dev.pavatus.stp.indexing.ServerWorldIndexer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class STPMod implements ModInitializer {

    public static final String MOD_ID = "stp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier INDEX_WORLDS = new Identifier(MOD_ID, "index_worlds");

    public static void moveToWorld(ServerPlayerEntity player, ServerWorld destination) {

    }

    /* (server)
    teleport -> player respawn packet
            |-> send world info -> player spawn position packet

       (client)
    player respawn packet -> player & world stuff -> set loading screen
    player spawn position packet -> remove loading screen
     */
    public static void teleport(ServerPlayerEntity player, ServerWorld targetWorld, Vec3d pos, float yaw, float pitch) {

    }

    @Override
    public void onInitialize() {
        ServerWorldIndexer.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            STPDebugCommand.register(dispatcher);
        });

        ChunkSyncEvents.SYNC.register((world, player, chunk) -> {

        });

        ChunkSyncEvents.DESYNC.register((world, player, chunk) -> {

        });
    }
}
