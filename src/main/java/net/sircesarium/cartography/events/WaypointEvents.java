package net.sircesarium.cartography.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.config.CartographyServerConfig;
import net.sircesarium.cartography.data.Waypoint;
import net.sircesarium.cartography.data.storage.WaypointData;
import net.sircesarium.cartography.helpers.EventHelper;
import net.sircesarium.cartography.helpers.LevelHelper;
import net.sircesarium.cartography.helpers.WaypointHelper;
import net.sircesarium.cartography.manager.WaypointManager;

import java.util.UUID;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Cartography.MODID)
public class WaypointEvents {

    @SubscribeEvent
    public static void onPlayerSetSpawn(PlayerSetSpawnEvent event) {
        EventHelper.safeServerEvent("save waypoint on spawn set", () -> {
            var level = LevelHelper.serverLevel(event.getEntity().level()).orElse(null);
            
            if (level == null) return;
            if (!CartographyServerConfig.saveWaypointOnRespawnChange.get()) return;
            if (event.getNewSpawn() == null) return;

            WaypointData data = WaypointManager.getOrCreate(level);
            BlockPos pos = event.getNewSpawn();

            if (data.hasWaypointAt(pos)) return;

            ServerPlayer player = (ServerPlayer) event.getEntity();
            BlockState state = level.getBlockState(pos);
            ResourceLocation icon = BuiltInRegistries.BLOCK.getKey(state.getBlock());

            data.addWaypoint(pos, level.dimension(), player.getDisplayName().getString() + "'s bed", null, icon, player.getUUID(), null);
            data.saveIfDirty();
        });
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        EventHelper.safeServerEvent("save waypoint on death", () -> {
            var level = LevelHelper.serverLevel(event.getEntity().level()).orElse(null);

            if (level == null) return;
            if (!event.isWasDeath()) return;
            if (!CartographyServerConfig.saveWaypointOnDeath.get()) return;

            ServerPlayer newPlayer = (ServerPlayer) event.getEntity();
            BlockPos deathPos = event.getOriginal().blockPosition();

            WaypointData data = WaypointManager.getOrCreate(level);

            if (data.hasWaypointAt(deathPos, level.dimension())) return;

            Long removeAt = WaypointHelper.computeExpiry(CartographyServerConfig.deathWaypointExpiry.get());

            data.addWaypoint(deathPos, level.dimension(), newPlayer.getDisplayName().getString() + "'s death", null, ResourceLocation.parse("minecraft:skeleton_skull"), newPlayer.getUUID(), removeAt);
            data.saveIfDirty();
        });
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        EventHelper.safeServerEvent("save waypoint on mount", () -> {
            var level = LevelHelper.serverLevel(event.getLevel()).orElse(null);

            if (level == null) return;
            if (!event.isMounting()) return;
            if (!(event.getEntityMounting() instanceof Player player)) return;
            if (!CartographyServerConfig.saveWaypointOnMount.get()) return;

            Entity vehicle = event.getEntityBeingMounted();

            if (!WaypointHelper.isAllowedVehicle(vehicle)) return;
            if (WaypointHelper.isVehicleBlacklisted(vehicle)) return;

            ResourceLocation item = WaypointHelper.resolveIcon(vehicle);

            BlockPos pos = vehicle.blockPosition();
            String entityName = vehicle.getDisplayName().getString();
            Long removeAt = WaypointHelper.computeExpiry(CartographyServerConfig.removeMountWaypointAfter.get());

            WaypointData data = WaypointManager.getOrCreate(level);
            UUID entityId = vehicle.getUUID();

            Waypoint existingById = data.getWaypoints().stream()
                    .filter(wp -> wp.getId().equals(entityId))
                    .findFirst()
                    .orElse(null);

            if (existingById != null) {
                data.updateWaypoint(new Waypoint(entityId, pos, existingById.getDimension(), existingById.getName(), existingById.getColor(), existingById.getIcon(), existingById.getAuthor(), removeAt));
            } else {
                if (data.hasWaypointAt(pos, level.dimension())) return;

                data.addWaypoint(entityId, pos, level.dimension(), entityName, null, item, player.getUUID(), removeAt);
            }

            data.saveIfDirty();
        });
    }

}
