package dev.pavatus.stp.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class SwapWorldRendererCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("stp").then(literal("swap-world-renderer")
                .then(argument("dimension", DimensionArgumentType.dimension())
                        .then(argument("pos", BlockPosArgumentType.blockPos())
                                .executes(SwapWorldRendererCommand::run)))));
    }

    private static int run(CommandContext<FabricClientCommandSource> ctx) {

        return Command.SINGLE_SUCCESS;
    }
}
