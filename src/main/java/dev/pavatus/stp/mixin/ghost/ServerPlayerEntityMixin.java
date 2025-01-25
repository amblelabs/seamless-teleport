package dev.pavatus.stp.mixin.ghost;

import com.mojang.authlib.GameProfile;
import dev.pavatus.stp.ghost.GhostServerPlayerEntity;
import dev.pavatus.stp.ghost.SServerPlayerEntity;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements SServerPlayerEntity {

    @Unique
    private Set<GhostServerPlayerEntity> ghosts;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(MinecraftServer server, ServerWorld world, GameProfile profile, CallbackInfo ci) {
        if ((Object) this instanceof GhostServerPlayerEntity)
            this.ghosts = new ReferenceOpenHashSet<>();
    }


    @Override
    public Collection<GhostServerPlayerEntity> stp$ghosts() {
        return ghosts;
    }
}
