package dev.pavatus.stp.indexing;

import dev.pavatus.stp.STPMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Collection;

public class ServerWorldIndexer {

    private static int lastIndex = 0;
    private static boolean serverStarted;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> serverStarted = true);

        ServerWorldEvents.LOAD.register((server, world)
                -> {
            if (!(world instanceof SServerWorld sworld))
                return;

            if (sworld.stp$hasIndex())
                return;

            sworld.stp$setIndex(lastIndex++);

            if (!serverStarted)
                return;

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                syncToPlayer(server, player);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server)
                -> syncToPlayer(server, handler.getPlayer()));
    }

    public static int getWorldIndex(ServerWorld world) {
        return ((SServerWorld) world).stp$index();
    }

    private static void syncToPlayer(MinecraftServer server, ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        Collection<ServerWorld> worlds = (Collection<ServerWorld>) server.getWorlds();

        buf.writeVarInt(worlds.size());

        int i = 0;
        for (ServerWorld world : worlds) {
            buf.writeRegistryKey(world.getRegistryKey());
            buf.writeVarInt(i++);
        }

        ServerPlayNetworking.send(player, STPMod.INDEX_WORLDS, buf);
    }
}
