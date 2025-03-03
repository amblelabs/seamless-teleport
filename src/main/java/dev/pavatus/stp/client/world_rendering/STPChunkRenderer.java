package dev.pavatus.stp.client.world_rendering;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import dev.pavatus.stp.client.ghost.GhostClientPlayerEntity;
import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import dev.pavatus.stp.client.indexing.SClientWorld;
import dev.pavatus.stp.mixin.client.ClientWorldRendererAccessor;
import dev.pavatus.stp.mixin.client.WorldRendererAccessor;
import dev.pavatus.stp.mixin.world_indexing.ClientWorldMixin;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.math.*;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

import static dev.pavatus.stp.client.STPModClient.WORLD_INDEX;

public class STPChunkRenderer {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final FramebufferHandler FB_HANDLER = new FramebufferHandler();
    private ChunkDeltaUpdateS2CPacket chunkDeltaUpdateS2CPacket;
    public STPChunkRenderer() {}

    public static STPChunkRenderer getInstance() {
        return new STPChunkRenderer();
    }

    public void setChunkDeltaUpdateS2CPacket(ChunkDeltaUpdateS2CPacket packet) {
        this.chunkDeltaUpdateS2CPacket = packet;
    }

    public void handleChunkPacket(ClientWorld world, MatrixStack matrixStack, ChunkDeltaUpdateS2CPacket delta) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
        if (delta == null) return;
        delta.visitUpdates((pos, state) -> {
            client.getBlockRenderManager().renderBlock(state, pos, world,
                    matrixStack, VertexConsumerProvider.immediate(bufferbuilder).getBuffer(RenderLayers.getBlockLayer(state)), true, world.random);
            if (world.getBlockEntity(pos) != null) {
                BlockEntity entity = world.getBlockEntity(pos);
                client.getBlockEntityRenderDispatcher().render(
                        entity,
                        MinecraftClient.getInstance().getTickDelta(),
                        matrixStack,
                        VertexConsumerProvider.immediate(bufferbuilder));
            }
        });
        tessellator.draw();
    }

    public void renderChunkFromTarget(float tickDelta, long startTime, MatrixStack stack, CallbackInfo ci) {
        if (client.world == null ||  client.player == null) return;
        MinecraftClient.getInstance().getFramebuffer().endWrite();
        FB_HANDLER.setupFramebuffer();

        copyFramebuffer(MinecraftClient.getInstance().getFramebuffer(), FB_HANDLER.afbo);

        ClientWorld world = ClientWorldIndexer.getWorld(WORLD_INDEX);

        if (world == null || world == client.world) return;

        ClientWorld oldWorld = client.world;

        ClientWorldRendererAccessor rendererAccessor = (ClientWorldRendererAccessor) world;

        client.world = world;

        GhostClientPlayerEntity ghost = ((SClientWorld) world).stp$getGhostPlayer();

        if (ghost == null)
            return;

        /*WorldRendererAccessor frustum = (WorldRendererAccessor) rendererAccessor.getWorldRenderer();
        WorldRendererAccessor frustum1 = (WorldRendererAccessor) client.worldRenderer;

        Vec3d offset = targetPosition.get().subtract(camera.getPos());*/

        rendererAccessor.getWorldRenderer().setWorld(world);
        MinecraftClient.getInstance().getFramebuffer().clear(MinecraftClient.IS_SYSTEM_MAC);

        /*stack.translate(offset.x, offset.y, offset.z);
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));*/
        Matrix4f matrix4f = stack.peek().getPositionMatrix();
        /*//frustum.setFrustum(frustum1.getFrustum());
        this.loadProjectionMatrix(matrix4f);
        Matrix3f matrix3f = new Matrix3f(stack.peek().getNormalMatrix()).invert();
        RenderSystem.setInverseViewRotationMatrix(matrix3f);
        rendererAccessor.getWorldRenderer().setupFrustum(stack, camera.getPos(), stack.peek().getPositionMatrix());*/
        Camera camera = ghost.getCamera();
        camera.update(client.world, ((SClientWorld) world).stp$getGhostPlayer(), !client.options.getPerspective().isFirstPerson(), true, tickDelta);
        //System.out.println(camera);
        this.handleChunkPacket(world, stack, this.chunkDeltaUpdateS2CPacket);
        rendererAccessor.getWorldRenderer().render(stack, tickDelta, startTime, false, camera,
                client.gameRenderer, client.gameRenderer.getLightmapTextureManager(),
                matrix4f);

        FB_HANDLER.afbo.beginWrite(true);
        //MinecraftClient.getInstance().getFramebuffer().clear(MinecraftClient.IS_SYSTEM_MAC);
        MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        FB_HANDLER.afbo.draw(MinecraftClient.getInstance().getFramebuffer().viewportWidth / 2, MinecraftClient.getInstance().getFramebuffer().viewportHeight / 2, false);
        rendererAccessor.getWorldRenderer().setWorld(oldWorld);
        client.world = oldWorld;
        client.setCameraEntity(client.player);
    }

    public void loadProjectionMatrix(Matrix4f projectionMatrix) {
        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorter.BY_DISTANCE);
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
