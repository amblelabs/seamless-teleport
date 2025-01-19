package dev.pavatus.stp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.pavatus.stp.ghost.GhostPlayerManager;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.ColumnPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class STPDebugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("stp-debug").then(
                CommandManager.argument("at-pos", BlockPosArgumentType.blockPos())
                        .then(CommandManager.argument("at-world", DimensionArgumentType.dimension())
                                .then(CommandManager.argument("player", EntityArgumentType.player())
                                        .executes(STPDebugCommand::run)))
        ));
    }

    private static int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "at-pos");
        ServerWorld world = DimensionArgumentType.getDimensionArgument(ctx, "at-world");
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

        ctx.getSource().getServer().executeSync(() -> {
            GhostPlayerManager.create(player, world, pos);
        });

        return Command.SINGLE_SUCCESS;
    }
}
