package dev.pavatus.stp.mixin.world_indexing;

import dev.pavatus.stp.client.ghost.GhostClientPlayNetworkHandler;
import dev.pavatus.stp.client.ghost.GhostClientPlayerEntity;
import dev.pavatus.stp.client.indexing.SClientWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.*;

@Mixin(ClientWorld.class)
public class ClientWorldMixin implements SClientWorld {

    @Shadow @Final private MinecraftClient client;

    @Unique private int index;
    @Unique private boolean init;

    @Unique private GhostClientPlayerEntity ghostPlayer;
    @Unique private GhostClientPlayNetworkHandler networkHandler;

    @Override
    public void stp$setIndex(int index) {
        this.index = index;
    }

    @Override
    public int stp$index() {
        return index;
    }

    @Unique
    private boolean isMain() {
        return (Object) this == MinecraftClient.getInstance().world;
    }

    @Override
    public ClientPlayNetworkHandler stp$networkHandler() {
        return /*this.isMain() ? this.client.getNetworkHandler() :*/ this.networkHandler;
    }

    @Override
    public ClientPlayerEntity stp$player() {
        return ghostPlayer;
    }

    @Override
    public void stp$setMain() {
        this.client.world = (ClientWorld) (Object) this;
    }

    @Override
    public void stp$unsetMain() {

    }

    @Override
    public void stp$init() {
        if (this.init)
            return;

        this.init = true;

        this.networkHandler = GhostClientPlayNetworkHandler.create(
                client.player);

        this.ghostPlayer = GhostClientPlayerEntity.create(
                (ClientWorld) (Object) this, networkHandler, client.player);

        /*if (this.isMain()) {
            this.stp$setMain();
        } else {
            this.stp$unsetMain();
        }*/
    }
}
