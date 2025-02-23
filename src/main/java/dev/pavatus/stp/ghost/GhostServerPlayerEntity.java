package dev.pavatus.stp.ghost;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class GhostServerPlayerEntity extends FakePlayer {

    private final ServerPlayerEntity owner;

    public GhostServerPlayerEntity(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        super(world, randomProfile());

        this.owner = player;
        this.networkHandler = GhostServerPlayNetworkHandler.create(this);

        this.setServerWorld(world);
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    private static GameProfile randomProfile() {
        UUID id = UUID.randomUUID();
        return new GameProfile(id, id.toString());
    }

    @Override
    public void playerTick() {
        super.playerTick();
        ServerChunkManager chunkManager = this.getServerWorld().getChunkManager();
        chunkManager.loadEntity(this);
    }

    public ServerPlayerEntity getOwner() {
        return owner;
    }

    @Override
    public void setStepHeight(float stepHeight) { }

    @Override
    public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) { }

    @Override
    public void refreshPositionAfterTeleport(Vec3d pos) { }

    @Override
    public void refreshPositionAfterTeleport(double x, double y, double z) { }

    @Override
    protected void refreshPosition() { }

    @Override
    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) { }

    @Override
    public void setPosition(double x, double y, double z) { }
}
