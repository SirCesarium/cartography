package net.sircesarium.cartography.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.config.CartographyServerConfig;
import net.sircesarium.cartography.data.storage.WaypointData;
import net.sircesarium.cartography.manager.WaypointManager;

@EventBusSubscriber(modid = "cartography")
public class WaypointEvents {
    @SubscribeEvent
    public static void onPlayerSetSpawn(PlayerSetSpawnEvent event) {
        try {
            if (event.getEntity().level().isClientSide()) return;
            if (!CartographyServerConfig.saveWaypointOnRespawnChange.get()) return;
            if (event.getNewSpawn() == null) return;

            ServerLevel level = (ServerLevel) event.getEntity().level();
            WaypointData data = WaypointManager.getOrCreate(level);

            BlockPos pos = event.getNewSpawn();

            boolean waypointExists = data.getWaypoints().stream()
                    .anyMatch(wp -> wp.getPos().equals(pos));

            if (waypointExists) return;

            ServerPlayer player = (ServerPlayer) event.getEntity();
            BlockState state = level.getBlockState(pos);
            ResourceLocation icon = BuiltInRegistries.BLOCK.getKey(state.getBlock());

            data.addWaypoint(pos, level.dimension(), player.getDisplayName().getString() + "'s bed", null, icon, player.getUUID(), null);
            data.saveIfDirty();
        } catch (Exception e) {
            Cartography.LOGGER.error("Failed to save waypoint on spawn set", e);
        }
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        try {
            if (event.getEntity().level().isClientSide()) return;
            if (!event.isMounting()) return;
            if (!(event.getEntityMounting() instanceof Player player)) return;
            if (!CartographyServerConfig.saveWaypointOnMount.get()) return;
            if (CartographyServerConfig.isVehicleBlacklisted(event.getEntityBeingMounted())) return;

            Entity vehicle = event.getEntityBeingMounted();
            ResourceLocation item;

            if (vehicle instanceof Boat boat) {
                item = BuiltInRegistries.ITEM.getKey(boat.getDropItem());
            } else if (vehicle instanceof Minecart) {
                item = BuiltInRegistries.ITEM.getKey(Items.MINECART);
            } else {
                item = BuiltInRegistries.ITEM.getKey(Items.SADDLE);
            }

            BlockPos pos = event.getEntityBeingMounted().blockPosition();
            ServerLevel level = (ServerLevel) event.getLevel();
            String entityName = event.getEntityBeingMounted().getDisplayName().getString();
            int seconds = CartographyServerConfig.removeMountWaypointAfter.get();
            Long removeAt = seconds > 0 ? System.currentTimeMillis() + (seconds * 1000L) : -1L;

            WaypointData data = WaypointManager.getOrCreate(level);

            data.addWaypoint(pos, level.dimension(), entityName, null, item, player.getUUID(), removeAt);
            data.saveIfDirty();
        } catch (Exception e) {
            Cartography.LOGGER.error("Failed to save waypoint on mount", e);
        }
    }
}
