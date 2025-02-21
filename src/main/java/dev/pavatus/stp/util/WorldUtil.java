package dev.pavatus.stp.util;

import dev.pavatus.stp.indexing.IndexableWorld;
import dev.pavatus.stp.mixin.access.ServerWorldInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestServer;
import net.minecraft.world.World;

import java.util.List;

public class WorldUtil {

    public static void addPlayer(ServerWorld world, ServerPlayerEntity player) {
        ((ServerWorldInvoker) world).stp$addPlayer(player);
    }

    public static int getWorldIndex(ServerWorld world) {
        return ((IndexableWorld.Holder) world.getServer()).stp$getWorldIndex((IndexableWorld) world);
    }

    @Environment(EnvType.CLIENT)
    public static int getWorldIndex(ClientWorld world) {
        return ((IndexableWorld.Holder) MinecraftClient.getInstance()).stp$getWorldIndex((IndexableWorld) world);
    }

    @SuppressWarnings("unchecked")
    public static <T extends World & IndexableWorld> T getWorld(MinecraftServer server, int index) {
        return (T) ((IndexableWorld.Holder) server).stp$get(index);
    }

    @Environment(EnvType.CLIENT)
    @SuppressWarnings("unchecked")
    public static <T extends World & IndexableWorld> T getWorld(MinecraftClient client, int index) {
        return (T) ((IndexableWorld.Holder) client).stp$get(index);
    }

    public static List<IndexableWorld> getWorlds(MinecraftServer server) {
        return ((IndexableWorld.Holder) server).stp$worlds();
    }
}
