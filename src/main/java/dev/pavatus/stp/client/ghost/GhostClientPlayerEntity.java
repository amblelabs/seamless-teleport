package dev.pavatus.stp.client.ghost;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;

public class GhostClientPlayerEntity extends ClientPlayerEntity {
    private Camera camera;
    public GhostClientPlayerEntity(ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting) {
        super(MinecraftClient.getInstance(), world, networkHandler, stats, recipeBook, lastSneaking, lastSprinting);
    }

    public static GhostClientPlayerEntity create(ClientWorld world, ClientPlayNetworkHandler networkHandler, ClientPlayerEntity original) {
        GhostClientPlayerEntity ghost = new GhostClientPlayerEntity(world, networkHandler, original.getStatHandler(), original.getRecipeBook(), false, false);
        ghost.setCamera(new Camera());
        return ghost;
    }

    @Override
    public void tickMovement() {
        if (client.player == null) return;
        this.setPosition(client.player.getPos());
        this.setHeadYaw(client.player.getHeadYaw());
        this.setPitch(client.player.getPitch());
    }

    public Camera getCamera() {
        return this.camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        //this.camera.update(this.clientWorld, this, false, true, MinecraftClient.getInstance().getTickDelta());
    }
}
