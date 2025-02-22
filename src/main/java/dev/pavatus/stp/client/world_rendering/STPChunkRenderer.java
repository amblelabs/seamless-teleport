package dev.pavatus.stp.client.world_rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import dev.pavatus.stp.client.indexing.SClientWorld;
import dev.pavatus.stp.client.interworld.ClientInterWorldPacketHandler;
import dev.pavatus.stp.mixin.client.ClientWorldRendererAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ChunkRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.ChunkRendererRegionBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;

import static dev.pavatus.stp.client.STPModClient.WORLD_INDEX;

public class STPChunkRenderer {
    private final MinecraftClient client = MinecraftClient.getInstance();
    public STPChunkRenderer() {}

    public void renderChunkFromTarget(WorldRenderContext context) {
        MatrixStack stack = context.matrixStack();
        Camera camera = context.camera();
        Vec3d targetPosition = new Vec3d(30, 100, 25);
        Vec3d offset = targetPosition.subtract(camera.getPos());
        if (client.world == null ||  client.player == null) return;


        ClientWorld world = ClientWorldIndexer.getWorld(WORLD_INDEX);

        if (world == null) return;

        ClientWorld oldWorld = client.world;

        ClientWorldRendererAccessor rendererAccessor = (ClientWorldRendererAccessor) world;

        client.world = world;

        rendererAccessor.getWorldRenderer().setWorld(world);

        stack.push();

        try {
            stack.translate(offset.x, offset.y, offset.z);
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));

            Matrix4f matrix4f = stack.peek().getPositionMatrix();
            RenderSystem.setProjectionMatrix(matrix4f, VertexSorter.BY_DISTANCE);

            rendererAccessor.getWorldRenderer().render(stack, client.getTickDelta(), 0, false, client.gameRenderer.getCamera(),
                    client.gameRenderer, client.gameRenderer.getLightmapTextureManager(),
                    matrix4f);
        } finally {
            stack.pop();
        }

        rendererAccessor.getWorldRenderer().setWorld(oldWorld);
        client.world = oldWorld;
    }
}
