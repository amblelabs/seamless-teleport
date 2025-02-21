package dev.pavatus.stp.ghost;

import com.mojang.authlib.GameProfile;
import dev.pavatus.stp.util.WorldUtil;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GhostPlayerManager {

    private static GhostPlayerManager instance;

    public static void init(MinecraftServer server) {
        instance = new GhostPlayerManager(server);
    }

    private final MinecraftServer server;
    private final List<GhostPlayerEntity> ghosts = new ArrayList<>();

    private GhostPlayerManager(MinecraftServer server) {
        this.server = server;
    }

    public void create(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        UUID id = UUID.randomUUID();

        GhostPlayerEntity ghost = new GhostPlayerEntity(world, new GameProfile(id, id.toString()), player);
        ghost.setPosition(pos.toCenterPos());

        WorldUtil.addPlayer(world, ghost);
    }

    public List<GhostPlayerEntity> ghosts() {
        return ghosts;
    }

    public static GhostPlayerManager get() {
        return instance;
    }
}
