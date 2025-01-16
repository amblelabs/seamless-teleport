package dev.pavatus.stp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.command.argument.ColumnPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

import java.lang.ref.WeakReference;

public class STPCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("stp").then(
                CommandManager.argument("at-pos", ColumnPosArgumentType.columnPos())
                        .then(CommandManager.argument("at-world", DimensionArgumentType.dimension())
                                .then(CommandManager.argument("world", DimensionArgumentType.dimension())
                                        .executes(STPCommand::run)))
        ));
    }

    private static int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ChunkPos chunkPos = ColumnPosArgumentType.getColumnPos(ctx, "at").toChunkPos();
        ServerWorld world = DimensionArgumentType.getDimensionArgument(ctx, "at-world");
        ServerWorld dest = DimensionArgumentType.getDimensionArgument(ctx, "world");

        ctx.getSource().getServer().executeSync(() -> {
            WorldChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);

            if (chunk instanceof STPChunk stpChunk)
                stpChunk.stp$linkedTo().add(new WeakReference<>(dest));
        });

        return Command.SINGLE_SUCCESS;
    }
}
