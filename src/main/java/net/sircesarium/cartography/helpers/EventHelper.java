package net.sircesarium.cartography.helpers;

import net.sircesarium.cartography.Cartography;

public final class EventHelper {

    private EventHelper() {
    }

    public static void safeServerEvent(String name, Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            Cartography.LOGGER.error("Failed to {}", name, e);
        }
    }
}
