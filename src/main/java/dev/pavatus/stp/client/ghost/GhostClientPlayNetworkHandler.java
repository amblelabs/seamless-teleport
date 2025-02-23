package dev.pavatus.stp.client.ghost;

import com.mojang.authlib.GameProfile;
import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Iterator;

import static dev.pavatus.stp.client.STPModClient.WORLD_INDEX;

public class GhostClientPlayNetworkHandler extends ClientPlayNetworkHandler {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final ClientWorld indexedWorld = ClientWorldIndexer.getWorld(WORLD_INDEX);
    private ClientWorld world = indexedWorld == null || indexedWorld == client.world ? client.world : ClientWorldIndexer.getWorld(WORLD_INDEX);

    public GhostClientPlayNetworkHandler(Screen screen, ClientConnection connection, @Nullable ServerInfo serverInfo, GameProfile profile, WorldSession worldSession) {
        super(MinecraftClient.getInstance(), screen, connection, serverInfo, profile, worldSession);
    }

    public static GhostClientPlayNetworkHandler create(ClientPlayerEntity player) {
        return new GhostClientPlayNetworkHandler(null, player.networkHandler.getConnection(), player.networkHandler.getServerInfo(), player.getGameProfile(), null);
    }

    @Override
    public void onChunkData(ChunkDataS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        int i = packet.getX();
        int j = packet.getZ();
        this.loadChunk(i, j, packet.getChunkData());
        LightData lightData = packet.getLightData();
        this.world.enqueueChunkUpdate(() -> {
            this.readLightData(i, j, lightData);
            WorldChunk worldChunk = this.world.getChunkManager().getWorldChunk(i, j, false);
            if (worldChunk != null) {
                this.scheduleRenderChunk(worldChunk, i, j);
            }
        });
    }

    private void scheduleRenderChunk(WorldChunk chunk, int x, int z) {
        LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
        ChunkSection[] chunkSections = chunk.getSectionArray();
        ChunkPos chunkPos = chunk.getPos();
        for (int i = 0; i < chunkSections.length; ++i) {
            ChunkSection chunkSection = chunkSections[i];
            int j = this.world.sectionIndexToCoord(i);
            lightingProvider.setSectionStatus(ChunkSectionPos.from(chunkPos, j), chunkSection.isEmpty());
            this.world.scheduleBlockRenders(x, j, z);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world == null || client.world == null || this.world == client.world)
            this.world = ClientWorldIndexer.getWorld(WORLD_INDEX);
    }

    private void loadChunk(int x, int z, ChunkData chunkData) {
        this.world.getChunkManager().loadChunkFromPacket(x, z, chunkData.getSectionsDataBuf(), chunkData.getHeightmap(), chunkData.getBlockEntities(x, z));
    }

    @Override
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        packet.visitUpdates((pos, state) -> this.world.handleBlockUpdate(pos, state, 19));
    }

    @Override
    public void onLightUpdate(LightUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, this.client);
        int i = packet.getChunkX();
        int j = packet.getChunkZ();
        LightData lightData = packet.getData();
        this.world.enqueueChunkUpdate(() -> this.readLightData(i, j, lightData));
    }

    private void readLightData(int x, int z, LightData data) {
        LightingProvider lightingProvider = this.world.getChunkManager().getLightingProvider();
        BitSet bitSet = data.getInitedSky();
        BitSet bitSet2 = data.getUninitedSky();
        Iterator<byte[]> iterator = data.getSkyNibbles().iterator();
        this.updateLighting(x, z, lightingProvider, LightType.SKY, bitSet, bitSet2, iterator);
        BitSet bitSet3 = data.getInitedBlock();
        BitSet bitSet4 = data.getUninitedBlock();
        Iterator<byte[]> iterator2 = data.getBlockNibbles().iterator();
        this.updateLighting(x, z, lightingProvider, LightType.BLOCK, bitSet3, bitSet4, iterator2);
        lightingProvider.setColumnEnabled(new ChunkPos(x, z), true);
    }

    private void updateLighting(int chunkX, int chunkZ, LightingProvider provider, LightType type, BitSet inited, BitSet uninited, Iterator<byte[]> nibbles) {
        for (int i = 0; i < provider.getHeight(); ++i) {
            int j = provider.getBottomY() + i;
            boolean bl = inited.get(i);
            boolean bl2 = uninited.get(i);
            if (!bl && !bl2) continue;
            provider.enqueueSectionData(type, ChunkSectionPos.from(chunkX, j, chunkZ), bl ? new ChunkNibbleArray((byte[])nibbles.next().clone()) : new ChunkNibbleArray());
            this.world.scheduleBlockRenders(chunkX, j, chunkZ);
        }
    }
}
