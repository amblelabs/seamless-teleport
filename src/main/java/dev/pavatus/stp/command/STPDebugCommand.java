package dev.pavatus.stp.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.pavatus.stp.interworld.InterWorldPacketHandler;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class STPDebugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("stp-debug").then(
                literal("create-ghost").then(
                argument("at-pos", BlockPosArgumentType.blockPos())
                        .then(argument("at-world", DimensionArgumentType.dimension())
                                .then(argument("player", EntityArgumentType.player())
                                        .executes(STPDebugCommand::createGhost))))
        ).then(
                literal("load-world").then(argument("world", DimensionArgumentType.dimension())
                        .then(argument("player", EntityArgumentType.player())
                                .executes(STPDebugCommand::loadWorld))
                )
        ));
    }

    private static int createGhost(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "at-pos");
        ServerWorld world = DimensionArgumentType.getDimensionArgument(ctx, "at-world");
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

        ctx.getSource().getServer().executeSync(() -> {
            InterWorldPacketHandler.createPacketWatcher(player, world, pos);
        });

        return Command.SINGLE_SUCCESS;
    }

    private static int loadWorld(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerWorld world = DimensionArgumentType.getDimensionArgument(ctx, "world");
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

        ctx.getSource().getServer().executeSync(() -> {
            InterWorldPacketHandler.loadWorld(player, world);
        });

        return Command.SINGLE_SUCCESS;
    }
}
