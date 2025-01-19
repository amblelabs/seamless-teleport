package dev.pavatus.stp.client.indexing;

import dev.pavatus.stp.STPMod;
import dev.pavatus.stp.client.event.ClientPlayerInitEvent;
import dev.pavatus.stp.client.event.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class ClientWorldIndexer {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(STPMod.INDEX_WORLDS, (client, handler, buf, responseSender) -> {
            int amount = buf.readVarInt();

            for (int i = 0; i < amount; i++) {
                ((SMinecraftClient) client).stp$setWorldIndex(
                        buf.readRegistryKey(RegistryKeys.WORLD), buf.readVarInt());
            }
        });

        ClientPlayerInitEvent.EVENT.register((client, player) -> {
            for (ClientWorld world : ((SMinecraftClient) client).stp$worlds()) {
                if (world == null)
                    continue;

                ((SClientWorld) world).stp$init();
            }
        });
    }

    public static int getWorldIndex(RegistryKey<World> key) {
        return ((SMinecraftClient) MinecraftClient.getInstance()).stp$getWorldIndex(key);
    }

    public static int getWorldIndex(ClientWorld world) {
        return ((SClientWorld) world).stp$index();
    }

    public static ClientWorld getWorld(int index) {
        return ((SMinecraftClient) MinecraftClient.getInstance()).stp$worlds().get(index);
    }
}
