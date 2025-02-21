package dev.pavatus.stp.ghost;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class GhostPlayerEntity extends FakePlayer {

    private final ServerPlayerEntity owner;

    public GhostPlayerEntity(ServerWorld world, GameProfile profile, ServerPlayerEntity owner) {
        super(world, profile);

        this.owner = owner;
        this.networkHandler = new GhostPlayNetworkHandler(this); // TODO
    }

    public ServerPlayerEntity owner() {
        return owner;
    }
}
