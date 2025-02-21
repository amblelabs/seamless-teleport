package dev.pavatus.stp.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class STPDebugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
//        dispatcher.register(literal("stp-debug").then(
//                literal("create-ghost").then(
//                argument("at-pos", BlockPosArgumentType.blockPos())
//                        .then(argument("at-world", DimensionArgumentType.dimension())
//                                .then(argument("player", EntityArgumentType.player())
//                                        .executes(STPDebugCommand::createGhost))))
//        ).then(
//                literal("load-world").then(argument("world", DimensionArgumentType.dimension())
//                        .then(argument("player", EntityArgumentType.player())
//                                .executes(STPDebugCommand::loadWorld))
//                )
//        ));
    }
}
