package dev.pavatus.stp.client.world_rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.util.Window;

public class FramebufferHandler {
    public Framebuffer afbo;

    public void setupFramebuffer() {
        Window window = MinecraftClient.getInstance().getWindow();

        if (afbo == null || afbo.textureWidth != window.getFramebufferWidth() || afbo.textureHeight != window.getFramebufferHeight()) {
            afbo = new SimpleFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight(), true, MinecraftClient.IS_SYSTEM_MAC);
            ;
        }

        afbo.beginWrite(false);
        afbo.checkFramebufferStatus();
    }

}
