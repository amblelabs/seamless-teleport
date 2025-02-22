package dev.pavatus.stp.client.interworld;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.Packet;

@FunctionalInterface
public interface ClientInterWorldPacketEvent {
    Event<ClientInterWorldPacketEvent> EVENT = EventFactory.createArrayBacked(
            ClientInterWorldPacketEvent.class, events -> (worldIndex, packet) -> {
                for (ClientInterWorldPacketEvent event : events) {
                    event.onPacket(worldIndex, packet);
                }
            }
    );

    void onPacket(int worldIndex, Packet<?> packet);
}
