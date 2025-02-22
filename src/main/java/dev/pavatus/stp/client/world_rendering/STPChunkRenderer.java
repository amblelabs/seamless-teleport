package dev.pavatus.stp.client.world_rendering;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import dev.pavatus.stp.client.ghost.GhostClientPlayerEntity;
import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import dev.pavatus.stp.mixin.client.ClientWorldRendererAccessor;
import dev.pavatus.stp.mixin.client.WorldRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

import static dev.pavatus.stp.client.STPModClient.WORLD_INDEX;

public class STPChunkRenderer {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final FramebufferHandler FB_HANDLER = new FramebufferHandler();
    public STPChunkRenderer() {}

    public void renderChunkFromTarget(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (client.world == null ||  client.player == null) return;
        MinecraftClient.getInstance().getFramebuffer().endWrite();
        FB_HANDLER.setupFramebuffer();

        ClientWorld world = ClientWorldIndexer.getWorld(WORLD_INDEX);

        if (world == null || world == client.world) return;

        ClientWorld oldWorld = client.world;

        ClientWorldRendererAccessor rendererAccessor = (ClientWorldRendererAccessor) world;

        WorldRendererAccessor frustum = (WorldRendererAccessor) rendererAccessor.getWorldRenderer();
        WorldRendererAccessor frustum1 = (WorldRendererAccessor) client.worldRenderer;

        client.world = world;

        MatrixStack stack = new MatrixStack();
        Camera camera = new Camera();
        AtomicReference<Vec3d> targetPosition = new AtomicReference<>(new Vec3d(0, 0, 0));
        client.world.getEntities().forEach(entity -> {
            if (entity instanceof GhostClientPlayerEntity ghost) {
                targetPosition.set(ghost.getPos());
                ghost.setPitch(client.player.getPitch());
                ghost.setHeadYaw(client.player.getHeadYaw());
                camera.update(world, ghost, false, false, tickDelta);
            }
        });

        Vec3d offset = targetPosition.get().subtract(camera.getPos());

        rendererAccessor.getWorldRenderer().setWorld(world);

        stack.translate(offset.x, offset.y, offset.z);
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw()));

        Matrix4f matrix4f = stack.peek().getPositionMatrix();
        frustum.setFrustum(frustum1.getFrustum());

        rendererAccessor.getWorldRenderer().render(stack, tickDelta, startTime, false, camera,
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
