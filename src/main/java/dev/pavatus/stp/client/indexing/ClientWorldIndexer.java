package dev.pavatus.stp.client.indexing;

import dev.pavatus.stp.STPMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.registry.RegistryKeys;

public class ClientWorldIndexer {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(STPMod.INDEX_WORLDS, (client, handler, buf, responseSender) -> {
            int amount = buf.readVarInt();

            for (int i = 0; i < amount; i++) {
                ((SMinecraftClient) client).stp$setWorldIndex(
                        buf.readRegistryKey(RegistryKeys.WORLD), buf.readVarInt());
            }
        });
    }
}
