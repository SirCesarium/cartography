package net.sircesarium.cartography.manager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.level.ServerLevel;
import net.sircesarium.cartography.data.storage.SubLevelData;

public class SubLevelManager {
    private static final ConcurrentHashMap<UUID, SubLevelData> cache = new ConcurrentHashMap<>();

    public static SubLevelData getOrCreate(ServerLevel level, UUID uuid) {
        return cache.computeIfAbsent(uuid, k -> {
            SubLevelData data = new SubLevelData(level, uuid);
            data.loadPose();
            return data;
        });
    }

    public static SubLevelData get(UUID uuid) {
        return cache.get(uuid);
    }

    public static SubLevelData remove(UUID uuid) {
        return cache.remove(uuid);
    }

    public static void saveAll() {
        cache.values().forEach(SubLevelData::saveIfDirty);
    }
}
