package net.sircesarium.cartography.config;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CartographyServerConfig {
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.BooleanValue saveWaypointOnRespawnChange;
    public static ModConfigSpec.BooleanValue saveWaypointOnMount;
    public static ModConfigSpec.BooleanValue saveWaypointOnDeath;
    public static ModConfigSpec.ConfigValue<List<? extends String>> mountBlacklist;
    public static ModConfigSpec.IntValue removeMountWaypointAfter;
    public static ModConfigSpec.IntValue deathWaypointExpiry;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        saveWaypointOnRespawnChange = builder
                .comment("Save a waypoint when a player changes their respawn")
                .define("saveWaypointOnRespawnChange", true);

        saveWaypointOnMount = builder
                .comment("Save temporary waypoints when a player mounts an entity")
                .define("saveWaypointOnMount", true);

        mountBlacklist = builder
                .comment("Entities that don't create waypoints when mounted",
                         "Use #namespace:tag for entity tags, or namespace:entity for specific entities")
                .defineList("mountBlacklist",
                        () -> List.of("minecraft:strider"),
                        () -> "",
                        obj -> obj instanceof String s && !s.isEmpty());

        removeMountWaypointAfter = builder
                .comment("Seconds before a mount waypoint is removed (0 = never)")
                .defineInRange("removeMountWaypointAfter", 300, 0, 7200);

        saveWaypointOnDeath = builder
                .comment("Save a temporary waypoint at death location")
                .define("saveWaypointOnDeath", true);

        deathWaypointExpiry = builder
                .comment("Seconds before a death waypoint is removed (0 = never)")
                .defineInRange("deathWaypointExpiry", 300, 0, 7200);

        SPEC = builder.build();
    }

    public static boolean isVehicleBlacklisted(Entity vehicle) {
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(vehicle.getType());

        for (String entry : mountBlacklist.get()) {
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
