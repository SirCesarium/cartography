package net.sircesarium.cartography.manager;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.sircesarium.cartography.data.storage.WaypointData;

public class WaypointManager {
    private static final ConcurrentHashMap<ResourceLocation, WaypointData> cache = new ConcurrentHashMap<>();

    public static WaypointData getOrCreate(ServerLevel level) {
        return cache.computeIfAbsent(level.dimension().location(), k -> {
            WaypointData data = new WaypointData(level);
            data.load();
            return data;
        });
    }

    public static void saveAll() {
        cache.values().forEach(WaypointData::saveIfDirty);
    }
}
