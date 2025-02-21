package dev.pavatus.stp.indexing;

import dev.pavatus.stp.util.WorldUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;

public class WorldIndexing {

    public static final Identifier PACKET_ID = new Identifier("stp", "index");

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            List<IndexableWorld> worlds = WorldUtil.getWorlds(server);
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeVarInt(worlds.size());
            for (IndexableWorld indexable : worlds) {

            }

            ServerPlayNetworking.send(player, PACKET_ID);
        });
    }
}
