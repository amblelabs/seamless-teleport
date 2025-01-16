package dev.pavatus.stp;

import dev.pavatus.stp.mixin.v2.EntityInvoker;
import dev.pavatus.stp.mixin.ServerPlayerEntityInvoker;
import dev.pavatus.stp.v2.STPServerPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.biome.source.BiomeAccess;

public class NastyNetworking {


    public static void moveToWorld(ServerPlayerEntity player, ServerWorld destination) {
        STPMod.LOGGER.info("Moving to world {} player {}", destination.getRegistryKey().getValue(), player.getName());
        ((ServerPlayerEntityInvoker) player).setInTeleportationState(true);

        ServerWorld serverWorld = player.getServerWorld();
        WorldProperties worldProperties = destination.getLevelProperties();

        sendTpPacket(player, destination);
        player.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));

        PlayerManager playerManager = player.getServer().getPlayerManager();
        playerManager.sendCommandTree(player);

        // it will automatically remove and unremove itself!
        ((STPServerPlayerEntity) player).stp$markPortaled();

        TeleportTarget teleportTarget = ((ServerPlayerEntityInvoker) player).stp$getTeleportTarget(destination);

        if (teleportTarget != null) {
            player.setServerWorld(destination);
            player.networkHandler.requestTeleport(teleportTarget.position.x, teleportTarget.position.y, teleportTarget.position.z, teleportTarget.yaw, teleportTarget.pitch);
            player.networkHandler.syncWithPlayerPosition();

            destination.onPlayerChangeDimension(player);

            ((ServerPlayerEntityInvoker) player).stp$worldChanged(serverWorld);
            player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.getAbilities()));

            playerManager.sendWorldInfo(player, destination);
            playerManager.sendPlayerStatus(player);

            for (StatusEffectInstance statusEffectInstance : player.getStatusEffects()) {
                player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), statusEffectInstance));
            }

            player.networkHandler.sendPacket(new WorldEventS2CPacket(WorldEvents.TRAVEL_THROUGH_PORTAL, BlockPos.ORIGIN, 0, false));

            ((ServerPlayerEntityInvoker) player).setSyncedExperience(-1);
            ((ServerPlayerEntityInvoker) player).setSyncedHealth(-1.0f);
            ((ServerPlayerEntityInvoker) player).setSyncedFoodLevel(-1);
        }
    }

    public static void handlePlayerTp(ServerPlayerEntity player, ServerWorld targetWorld, Vec3d pos, float yaw, float pitch) {
        if (player.getWorld() == targetWorld) {
            player.teleport(targetWorld, pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
            return;
        }

        STPMod.LOGGER.info("Teleporting {} to world {} at {}", player.getName(), targetWorld.getRegistryKey().getValue(), pos);

        player.setCameraEntity(player);
        player.stopRiding();

        ServerWorld serverWorld = player.getServerWorld();
        WorldProperties worldProperties = targetWorld.getLevelProperties();

        sendTpPacket(player, targetWorld);
        player.networkHandler.sendPacket(new DifficultyS2CPacket(worldProperties.getDifficulty(), worldProperties.isDifficultyLocked()));

        player.server.getPlayerManager().sendCommandTree(player);

        // it will automatically remove and unremove itself!
        ((STPServerPlayerEntity) player).stp$markPortaled();

        player.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
        player.setServerWorld(targetWorld);

        targetWorld.onPlayerTeleport(player);
        ((ServerPlayerEntityInvoker) player).stp$worldChanged(serverWorld);

        player.networkHandler.requestTeleport(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
        player.server.getPlayerManager().sendWorldInfo(player, targetWorld);
        player.server.getPlayerManager().sendPlayerStatus(player);
    }

    private static void sendTpPacket(ServerPlayerEntity player, ServerWorld targetWorld) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeRegistryKey(targetWorld.getDimensionKey());
        buf.writeRegistryKey(targetWorld.getRegistryKey());
        buf.writeLong(BiomeAccess.hashSeed(targetWorld.getSeed()));
        buf.writeByte(player.interactionManager.getGameMode().getId());
        buf.writeByte(GameMode.getId(player.interactionManager.getPreviousGameMode()));
        buf.writeBoolean(targetWorld.isDebugWorld());
        buf.writeBoolean(targetWorld.isFlat());
        buf.writeByte(3);
        buf.writeOptional(player.getLastDeathPos(), PacketByteBuf::writeGlobalPos);
        buf.writeVarInt(player.getPortalCooldown());

        ServerPlayNetworking.send(player, STPMod.TP, buf);
    }
}
