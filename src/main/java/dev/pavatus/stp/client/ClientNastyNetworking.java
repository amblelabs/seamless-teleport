package dev.pavatus.stp.client;

import dev.pavatus.stp.mixin.client.ClientPlayNetworkHandlerAccessor;
import dev.pavatus.stp.mixin.client.ClientWorldInvoker;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.map.MapState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClientNastyNetworking {

    public static void handleTp(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender response) {
        RegistryKey<DimensionType> dimensionTypeKey = buf.readRegistryKey(RegistryKeys.DIMENSION_TYPE);
        RegistryKey<World> worldKey = buf.readRegistryKey(RegistryKeys.WORLD);

        long sha256Seed = buf.readLong();
        GameMode gameMode = GameMode.byId(buf.readUnsignedByte());
        GameMode previousGameMode = GameMode.getOrNull(buf.readByte());
        boolean debugWorld = buf.readBoolean();
        boolean flatWorld = buf.readBoolean();
        byte flag = buf.readByte();
        Optional<GlobalPos> lastDeathPos = buf.readOptional(PacketByteBuf::readGlobalPos);
        int portalCooldown = buf.readVarInt();

        client.executeSync(() -> {
            if (client.player == null || client.world == null)
                return;

            if (!(client.getNetworkHandler() instanceof ClientPlayNetworkHandlerAccessor cpnha))
                return;

            RegistryEntry.Reference<DimensionType> dimensionTypeEntry = cpnha.getCombinedDynamicRegistries()
                    .getCombinedRegistryManager().get(RegistryKeys.DIMENSION_TYPE).entryOf(dimensionTypeKey);

            tp(cpnha, MinecraftClient.getInstance(), client.player, dimensionTypeEntry,
                    worldKey, sha256Seed, gameMode, previousGameMode, debugWorld,
                    flatWorld, flag, lastDeathPos, portalCooldown);
        });
    }

    private static void tp(ClientPlayNetworkHandlerAccessor accessor,
                           MinecraftClient client, ClientPlayerEntity player,
                           RegistryEntry.Reference<DimensionType> toDimensionEntry,
                           RegistryKey<World> toWorldKey, long sha256Seed, GameMode gameMode,
                           @Nullable GameMode previousGameMode, boolean debugWorld, boolean flatWorld,
                           byte flag, Optional<GlobalPos> lastDeathPos, int portalCooldown) {
        ClientWorld world = accessor.getWorld();
        int i = player.getId();

        if (toWorldKey != player.getWorld().getRegistryKey()) {
            ClientWorld.Properties oldProperties = accessor.getWorldProperties();
            ClientWorld.Properties newProperties = new ClientWorld.Properties(oldProperties.getDifficulty(), oldProperties.isHardcore(), flatWorld);

            Scoreboard scoreboard = world.getScoreboard();
            Map<String, MapState> map = ((ClientWorldInvoker) world).stp$getMapStates();

            accessor.setWorldProperties(newProperties);

            world = new ClientWorld(player.networkHandler, newProperties, toWorldKey, toDimensionEntry,
                    accessor.getChunkLoadDistance(), accessor.getSimulationDistance(), client::getProfiler,
                    client.worldRenderer, debugWorld, sha256Seed);

            accessor.setWorld(world);

            world.setScoreboard(scoreboard);
            ((ClientWorldInvoker) world).stp$putMapStates(map);

            ((STPMinecraftClient) client).stp$joinWorld(world);
        }

        String string = player.getServerBrand();
        client.cameraEntity = null;

        if (player.shouldCloseHandledScreenOnRespawn())
            player.closeHandledScreen();

        ClientPlayerEntity newPlayer = (flag & 2) != 0 ? client.interactionManager.createPlayer(world, player.getStatHandler(), player.getRecipeBook(), player.isSneaking(), player.isSprinting())
                : client.interactionManager.createPlayer(world, player.getStatHandler(), player.getRecipeBook());
        newPlayer.setId(i);

        client.player = newPlayer;

        if (toWorldKey != player.getWorld().getRegistryKey())
            client.getMusicTracker().stop();

        client.cameraEntity = newPlayer;

        List<DataTracker.SerializedEntry<?>> list = player.getDataTracker().getChangedEntries();

        if ((flag & 2) != 0 && list != null)
            newPlayer.getDataTracker().writeUpdatedEntries(list);

        if ((flag & 1) != 0)
            newPlayer.getAttributes().setFrom(player.getAttributes());

        newPlayer.init();
        newPlayer.setServerBrand(string);
        world.addPlayer(i, newPlayer);
        newPlayer.setYaw(-180.0f);
        newPlayer.input = new KeyboardInput(client.options);

        client.interactionManager.copyAbilities(newPlayer);

        newPlayer.setReducedDebugInfo(player.hasReducedDebugInfo());
        newPlayer.setShowsDeathScreen(player.showsDeathScreen());
        newPlayer.setLastDeathPos(lastDeathPos);
        newPlayer.setPortalCooldown(portalCooldown);

        newPlayer.nauseaIntensity = player.nauseaIntensity;
        newPlayer.prevNauseaIntensity = player.prevNauseaIntensity;

        if (client.currentScreen instanceof DeathScreen || client.currentScreen instanceof DeathScreen.TitleScreenConfirmScreen)
            client.setScreen(null);

        client.interactionManager.setGameModes(gameMode, previousGameMode);
    }
}
