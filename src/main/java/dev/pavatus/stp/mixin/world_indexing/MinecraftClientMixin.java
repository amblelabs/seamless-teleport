package dev.pavatus.stp.mixin.world_indexing;

import dev.pavatus.stp.client.indexing.SClientWorld;
import dev.pavatus.stp.client.indexing.SMinecraftClient;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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
    @Unique private final List<ClientWorld> worlds = new ArrayList<>();

    @Override
    public List<ClientWorld> stp$worlds() {
        return worlds;
    }

    @Override
    public Integer stp$getWorldIndex(RegistryKey<World> key) {
        return keyToWorldIndex.get(key);
    }

    @Override
    public void stp$setWorldIndex(RegistryKey<World> key, int index) {
        keyToWorldIndex.put(key, index);

        if (index >= worlds.size())
            worlds.add(null);

        System.out.println("Received world: " + key + ": " + index);
    }

    @Inject(method = "joinWorld", at = @At("HEAD"))
    public void joinWorld(ClientWorld world, CallbackInfo ci) {
        if (!(world instanceof SClientWorld sworld))
            return;

        int index = this.stp$getWorldIndex(world.getRegistryKey());
        sworld.stp$setIndex(index);

        this.worlds.set(index, world);
    }
}
