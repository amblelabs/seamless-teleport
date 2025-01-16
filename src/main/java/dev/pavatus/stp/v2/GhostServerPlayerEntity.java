package dev.pavatus.stp.v2;

import net.minecraft.server.network.ServerPlayerEntity;

public class GhostServerPlayerEntity extends ServerPlayerEntity {

    private final ServerPlayerEntity real;

    public GhostServerPlayerEntity(ServerPlayerEntity real) {
        super(real.getServer(), real.getServerWorld(), real.getGameProfile());

        this.real = real;

        this.setPos(real.getX(),real.getY(),real.getZ());

        this.setYaw(real.getYaw());
        this.setBodyYaw(real.getBodyYaw());
        this.setHeadYaw(real.getHeadYaw());
        this.setPitch(real.getPitch());

        this.networkHandler = new STPServerPlayNetworkHandler(this.server, real.networkHandler, this);
    }
}
