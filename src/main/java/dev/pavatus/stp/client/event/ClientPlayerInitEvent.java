package dev.pavatus.stp.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

public class ClientPlayerInitEvent {
    public static final Event<ClientPlayerInit> EVENT = EventFactory.createArrayBacked(ClientPlayerInit.class,
            callbacks -> (client, world) -> {
                for (ClientPlayerInit callback : callbacks) {
                    callback.onClientPlayerInit(client, world);
                }
            });

    @FunctionalInterface
    public interface ClientPlayerInit {
        void onClientPlayerInit(MinecraftClient client, ClientPlayerEntity player);
    }
}
