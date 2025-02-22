package dev.pavatus.stp.client.world_rendering;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import dev.pavatus.stp.client.indexing.SClientWorld;
import dev.pavatus.stp.client.interworld.ClientInterWorldPacketHandler;
import dev.pavatus.stp.mixin.client.ClientWorldRendererAccessor;
import dev.pavatus.stp.mixin.client.WorldRendererAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.ChunkRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
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
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev.pavatus.stp.client.STPModClient.WORLD_INDEX;

public class STPChunkRenderer {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final FramebufferHandler FB_HANDLER = new FramebufferHandler();
    public STPChunkRenderer() {}

    public void renderChunkFromTarget(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        MinecraftClient.getInstance().getFramebuffer().endWrite();
        FB_HANDLER.setupFramebuffer();
        MatrixStack stack = new MatrixStack();
        Camera camera = client.gameRenderer.getCamera();
        Vec3d targetPosition = new Vec3d(0, 64, 0);
        Vec3d offset = targetPosition.subtract(camera.getPos());
        if (client.world == null ||  client.player == null) return;


        ClientWorld world = ClientWorldIndexer.getWorld(WORLD_INDEX);

        if (world == null) return;

        ClientWorld oldWorld = client.world;

        ClientWorldRendererAccessor rendererAccessor = (ClientWorldRendererAccessor) world;

        WorldRendererAccessor frustum = (WorldRendererAccessor) rendererAccessor.getWorldRenderer();
        WorldRendererAccessor frustum1 = (WorldRendererAccessor) client.worldRenderer;

        client.world = world;

        rendererAccessor.getWorldRenderer().setWorld(world);

        stack.translate(offset.x, offset.y, offset.z);
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));

        Matrix4f matrix4f = stack.peek().getPositionMatrix();
        frustum.setFrustum(frustum1.getFrustum());

        rendererAccessor.getWorldRenderer().render(stack, tickDelta, startTime, false, client.gameRenderer.getCamera(),
                client.gameRenderer, client.gameRenderer.getLightmapTextureManager(),
                matrix4f);

        FB_HANDLER.afbo.beginWrite(false);

        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        FB_HANDLER.afbo.draw(MinecraftClient.getInstance().getFramebuffer().viewportHeight / 2, MinecraftClient.getInstance().getFramebuffer().viewportWidth / 2);
        rendererAccessor.getWorldRenderer().setWorld(oldWorld);
        client.world = oldWorld;
    }

    private static void copyFramebuffer(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT | GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    private static void copyColor(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_COLOR_BUFFER_BIT, GlConst.GL_NEAREST);
    }

    private static void copyDepth(Framebuffer src, Framebuffer dest) {
        GlStateManager._glBindFramebuffer(GlConst.GL_READ_FRAMEBUFFER, src.fbo);
        GlStateManager._glBindFramebuffer(GlConst.GL_DRAW_FRAMEBUFFER, dest.fbo);
        GlStateManager._glBlitFrameBuffer(0, 0, src.textureWidth, src.textureHeight, 0, 0, dest.textureWidth, dest.textureHeight, GlConst.GL_DEPTH_BUFFER_BIT, GlConst.GL_NEAREST);
    }
}
