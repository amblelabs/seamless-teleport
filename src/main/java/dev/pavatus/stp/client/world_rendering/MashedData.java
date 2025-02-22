package dev.pavatus.stp.client.world_rendering;


import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.LightData;

public record MashedData(ChunkData chunkData, LightData lightData) {
}
