package dev.pavatus.stp.core.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ColumnPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

public class STPDebugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("stp-debug").then(
                CommandManager.argument("at-pos", ColumnPosArgumentType.columnPos())
                        .then(CommandManager.argument("at-world", DimensionArgumentType.dimension())
                                .then(CommandManager.argument("world", DimensionArgumentType.dimension())
                                        .executes(STPDebugCommand::run)))
        ));
    }

    private static int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ChunkPos chunkPos = ColumnPosArgumentType.getColumnPos(ctx, "at").toChunkPos();
        ServerWorld world = DimensionArgumentType.getDimensionArgument(ctx, "at-world");
        ServerWorld dest = DimensionArgumentType.getDimensionArgument(ctx, "world");

        ctx.getSource().getServer().executeSync(() -> {

        });

        return Command.SINGLE_SUCCESS;
    }
}
