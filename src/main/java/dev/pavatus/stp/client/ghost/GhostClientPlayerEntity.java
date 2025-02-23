package dev.pavatus.stp.client.ghost;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;

public class GhostClientPlayerEntity extends ClientPlayerEntity {
    public GhostClientPlayerEntity(ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting) {
        super(MinecraftClient.getInstance(), world, networkHandler, stats, recipeBook, lastSneaking, lastSprinting);
    }

    public static GhostClientPlayerEntity create(ClientWorld world, ClientPlayNetworkHandler networkHandler, ClientPlayerEntity original) {
        return new GhostClientPlayerEntity(world, networkHandler, original.getStatHandler(), original.getRecipeBook(), false, false);
    }
}
