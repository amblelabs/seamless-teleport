package dev.pavatus.stp.mixin.world_indexing;

import dev.pavatus.stp.client.indexing.SClientWorld;
import dev.pavatus.stp.client.indexing.SMinecraftClient;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements SMinecraftClient {

    @Unique private final Object2IntMap<RegistryKey<World>> keyToWorldIndex = new Object2IntOpenHashMap<>();
    @Unique private final List<SClientWorld> worlds = new ArrayList<>();

    @Override
    public List<SClientWorld> stp$worlds() {
        return worlds;
    }

    @Override
    public Integer stp$getWorldIndex(RegistryKey<World> key) {
        return keyToWorldIndex.get(key);
    }

    @Override
    public void stp$setWorldIndex(RegistryKey<World> key, int index) {
        keyToWorldIndex.put(key, index);

        System.out.println("Received worlds: " + key + ": " + index);
    }

    @Inject(method = "joinWorld", at = @At("HEAD"))
    public void joinWorld(ClientWorld world, CallbackInfo ci) {
        if (world instanceof SClientWorld sworld)
            this.worlds.add(sworld);
    }
}
