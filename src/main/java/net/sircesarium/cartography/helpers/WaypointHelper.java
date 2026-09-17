package net.sircesarium.cartography.helpers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Items;
import net.sircesarium.cartography.config.CartographyServerConfig;

@SuppressWarnings("unused")
public final class WaypointHelper {

    private WaypointHelper() {
    }

    public static Long computeExpiry(int seconds) {
        if (seconds <= 0) return -1L;
        return System.currentTimeMillis() + (seconds * 1000L);
    }

    public static boolean isAllowedVehicle(Entity entity) {
        return entity instanceof Mob || entity instanceof Boat || entity instanceof Minecart;
    }

    public static ResourceLocation resolveIcon(Entity vehicle) {
        if (vehicle instanceof Boat boat) {
            return BuiltInRegistries.ITEM.getKey(boat.getDropItem());
        } else if (vehicle instanceof Minecart) {
            return BuiltInRegistries.ITEM.getKey(Items.MINECART);
        }

        return BuiltInRegistries.ITEM.getKey(Items.SADDLE);
    }

    public static boolean isVehicleBlacklisted(Entity vehicle) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(vehicle.getType());

        for (String entry : CartographyServerConfig.mountBlacklist.get()) {
            if (entry.startsWith("#")) {
                ResourceLocation tagLoc = ResourceLocation.parse(entry.substring(1));
                TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, tagLoc);

                if (vehicle.getType().is(tag)) return true;
            } else {
                ResourceLocation id = ResourceLocation.parse(entry);

                if (key.equals(id)) return true;
            }
        }
        return false;
    }
}
