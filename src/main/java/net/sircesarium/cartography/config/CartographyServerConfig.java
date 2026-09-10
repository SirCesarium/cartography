package net.sircesarium.cartography.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CartographyServerConfig {
    public static final ModConfigSpec SPEC;

    public static ModConfigSpec.BooleanValue saveWaypointOnSleep;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        saveWaypointOnSleep = builder
                .comment("Save waypoints when a player sleeps")
                .define("saveWaypointOnSleep", true);

        SPEC = builder.build();
    }
}
