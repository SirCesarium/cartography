package net.sircesarium.cartography.config;

import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CartographyServerConfig {
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.BooleanValue saveWaypointOnRespawnChange;
    public static ModConfigSpec.BooleanValue saveWaypointOnMount;
    public static ModConfigSpec.BooleanValue saveWaypointOnDeath;
    public static ModConfigSpec.ConfigValue<List<? extends String>> mountBlacklist;
    public static ModConfigSpec.IntValue removeMountWaypointAfter;
    public static ModConfigSpec.IntValue deathWaypointExpiry;

    public static ModConfigSpec.EnumValue<F3Restriction> hideF3Coordinates;
    public static ModConfigSpec.EnumValue<F3Restriction> hideF3Block;
    public static ModConfigSpec.EnumValue<F3Restriction> hideF3Chunk;
    public static ModConfigSpec.EnumValue<F3Restriction> hideF3Facing;
    public static ModConfigSpec.EnumValue<F3Restriction> hideF3Biome;
    public static ModConfigSpec.EnumValue<F3Restriction> hideF3Targeted;
    public static ModConfigSpec.EnumValue<F3Restriction> hideF3Light;
    public static ModConfigSpec.EnumValue<F3Restriction> disableF3C;
    public static ModConfigSpec.EnumValue<F3Restriction> disableF3G;

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

        builder.comment("F3 debug screen restrictions").push("f3");

        hideF3Coordinates = builder
                .comment("Hide XYZ coordinates in F3 overlay",
                        "DISABLED: no restriction, NON_OPS: non-ops only, ALL: everyone")
                .defineEnum("hideF3Coordinates", F3Restriction.DISABLED);

        hideF3Block = builder
                .comment("Hide block position in F3 overlay")
                .defineEnum("hideF3Block", F3Restriction.DISABLED);

        hideF3Chunk = builder
                .comment("Hide chunk coordinates in F3 overlay")
                .defineEnum("hideF3Chunk", F3Restriction.DISABLED);

        hideF3Facing = builder
                .comment("Hide facing direction in F3 overlay")
                .defineEnum("hideF3Facing", F3Restriction.DISABLED);

        hideF3Biome = builder
                .comment("Hide biome in F3 overlay")
                .defineEnum("hideF3Biome", F3Restriction.DISABLED);

        hideF3Targeted = builder
                .comment("Hide targeted block in F3 overlay")
                .defineEnum("hideF3Targeted", F3Restriction.DISABLED);

        hideF3Light = builder
                .comment("Hide client light level in F3 overlay")
                .defineEnum("hideF3Light", F3Restriction.DISABLED);

        disableF3C = builder
                .comment("Disable F3+C copy position to clipboard")
                .defineEnum("disableF3C", F3Restriction.DISABLED);

        disableF3G = builder
                .comment("Disable F3+G chunk border toggle")
                .defineEnum("disableF3G", F3Restriction.DISABLED);

        builder.pop();

        SPEC = builder.build();
    }
}
