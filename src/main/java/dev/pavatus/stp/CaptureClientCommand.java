package dev.pavatus.stp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public class CaptureClientCommand {

    private static ClientChunkCache cache;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("capture-client")
                .executes(context -> {
                    MinecraftClient client = context.getSource().getClient();

                    client.executeSync(() -> {
                        if (cache != null)
                            cache.free();

                        cache = ClientChunkCache.capture(client.worldRenderer);
                    });

                    return Command.SINGLE_SUCCESS;
                }));

        dispatcher.register(ClientCommandManager.literal("restore-client")
                .executes(context -> {
                    if (cache == null) {
                        STPMod.LOGGER.error("no cache found");
                        return 0;
                    }

                    MinecraftClient client = context.getSource().getClient();

                    client.executeSync(() -> {
                        cache.restore(client.worldRenderer);
                        cache = null;
                    });

                    return Command.SINGLE_SUCCESS;
                }));
    }
}
