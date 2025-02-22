package dev.pavatus.stp;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public record ClientChunkCache(
        BlockingQueue<ChunkBuilder.BuiltChunk> builtChunks,
        BuiltChunkStorage chunks,
        ChunkBuilder chunkBuilder,
        WorldRenderer.RenderableChunks renderableChunks,
        ObjectArrayList<WorldRenderer.ChunkInfo> chunkInfos
) {

    public static ClientChunkCache capture(WorldRenderer worldRenderer) {
        BlockingQueue<ChunkBuilder.BuiltChunk> builtChunks = new LinkedBlockingQueue<>(worldRenderer.builtChunks);

        ChunkBuilder chunkBuilder = worldRenderer.chunkBuilder;
        ((Capturable) chunkBuilder).setCaptured(true);

        BuiltChunkStorage chunks = worldRenderer.chunks;
        ((Capturable) chunks).setCaptured(true);

        WorldRenderer.RenderableChunks ogRenderableChunks = worldRenderer.renderableChunks.get();
        WorldRenderer.RenderableChunks copiedRenderableChunks = new WorldRenderer.RenderableChunks(ogRenderableChunks.chunkInfoList.current.length);

        ObjectArrayList<WorldRenderer.ChunkInfo> chunkInfos = new ObjectArrayList<>(worldRenderer.chunkInfos);
        copiedRenderableChunks.chunks.addAll(chunkInfos);

        System.arraycopy(ogRenderableChunks.chunkInfoList.current, 0, copiedRenderableChunks.chunkInfoList.current, 0, ogRenderableChunks.chunkInfoList.current.length);

        return new ClientChunkCache(builtChunks, chunks, chunkBuilder, copiedRenderableChunks, chunkInfos);
    }

    public void restore(WorldRenderer worldRenderer) {
        worldRenderer.builtChunks.clear();
        worldRenderer.builtChunks.addAll(builtChunks);

        // chunks
        worldRenderer.chunks.clear();
        worldRenderer.chunks = null;

        ((Capturable) this.chunks).setCaptured(false);
        worldRenderer.chunks = chunks;

        // chunk builder
        worldRenderer.chunkBuilder.stop();
        worldRenderer.chunkBuilder = null;

        ((Capturable) this.chunkBuilder).setCaptured(false);
        worldRenderer.chunkBuilder = chunkBuilder;

        // renderable chunks
        worldRenderer.renderableChunks.set(renderableChunks);

        // chunk infos
        worldRenderer.chunkInfos.clear();
        worldRenderer.chunkInfos.addAll(chunkInfos);
    }

    public void free() {
        this.builtChunks.clear();

        ((Capturable) this.chunks).setCaptured(false);
        this.chunks.clear();

        this.chunkBuilder.stop();
        this.chunkInfos.clear();
    }
}
