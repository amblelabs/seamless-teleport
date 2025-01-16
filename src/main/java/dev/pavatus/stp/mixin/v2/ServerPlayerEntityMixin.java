package dev.pavatus.stp.mixin.v2;

import dev.pavatus.stp.v2.STPServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements STPServerPlayerEntity {

    @Shadow public abstract ServerWorld getServerWorld();

    @Unique
    private boolean portaled;

    @Override
    public void stp$markPortaled() {
        this.getServerWorld().removePlayer((ServerPlayerEntity) (Object) this,
                Entity.RemovalReason.CHANGED_DIMENSION);

        this.portaled = true;
    }

    @Override
    public void stp$unsetPortaled() {
        this.portaled = false;
    }

    @Override
    public boolean stp$isPortaled() {
        return portaled;
    }
}
