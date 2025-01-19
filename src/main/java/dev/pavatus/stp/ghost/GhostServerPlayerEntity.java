package dev.pavatus.stp.ghost;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class GhostServerPlayerEntity extends ServerPlayerEntity {

    private final ServerPlayerEntity owner;

    public GhostServerPlayerEntity(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
        super(player.server, world, player.getGameProfile());

        this.owner = player;
        this.networkHandler = GhostServerPlayNetworkHandler.create(this);

        this.setPos(pos.getX(), pos.getY(), pos.getZ());
        this.setUuid(UUID.randomUUID());
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
}
