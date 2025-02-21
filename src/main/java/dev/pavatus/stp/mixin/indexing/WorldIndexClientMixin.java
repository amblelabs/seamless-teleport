package dev.pavatus.stp.mixin.indexing;

import dev.pavatus.stp.indexing.IndexableWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class WorldIndexClientMixin implements IndexableWorld.Holder {

    @Unique private final List<IndexableWorld> worlds = new ArrayList<>();

    @Override
    public List<IndexableWorld> stp$worlds() {
        return worlds;
    }

    @Override
    public void stp$refresh() { }

    @Override
    public void stp$add(IndexableWorld world) {
        if (world.stp$getIndex() == -1)
            world.stp$setIndex(this.worlds.size());

        this.worlds.add(world);
    }

    @Override
    public void stp$remove(IndexableWorld world) {
        if (world.stp$getIndex() == -1)
            return;

        this.worlds.set(world.stp$getIndex(), null);
    }

    @Override
    public IndexableWorld stp$get(int index) {
        return this.worlds.get(index);
    }

    @Override
    public int stp$getWorldIndex(IndexableWorld world) {
        return world.stp$getIndex();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    public void disconnect(Screen screen, CallbackInfo ci) {
        this.worlds.clear();
    }
}
