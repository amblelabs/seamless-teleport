package dev.pavatus.stp.client;

import dev.pavatus.stp.client.indexing.ClientWorldIndexer;
import dev.pavatus.stp.client.interworld.ClientInterWorldPacketEvent;
import dev.pavatus.stp.client.interworld.ClientInterWorldPacketHandler;
import dev.pavatus.stp.client.world_rendering.STPChunkRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class STPModClient implements ClientModInitializer {

    public static int WORLD_INDEX = 0;

    @Override
    public void onInitializeClient() {
        ClientWorldIndexer.init();
        ClientInterWorldPacketHandler.init();

        /*ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register();
        });*/
        ClientInterWorldPacketEvent.EVENT.register(((worldIndex, packet) -> {
            WORLD_INDEX = worldIndex;
        }));
    }
}
