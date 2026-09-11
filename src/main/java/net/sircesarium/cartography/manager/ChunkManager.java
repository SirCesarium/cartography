package net.sircesarium.cartography.manager;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.sircesarium.cartography.data.storage.ChunkData;

public class ChunkManager {
    private static final ConcurrentHashMap<String, ChunkData> cache = new ConcurrentHashMap<>();

    private static String chunkKey(ResourceKey<Level> dimension, int chunkX, int chunkZ) {
        return dimension.location() + ":" + chunkX + ":" + chunkZ;
    }

    public static ChunkData getOrCreate(ServerLevel level, ChunkPos pos) {
        String key = chunkKey(level.dimension(), pos.x, pos.z);
        return cache.computeIfAbsent(key, k -> {
            ChunkData data = new ChunkData(level, pos.x, pos.z);
            data.load();
            return data;
        });
    }

    public static ChunkData remove(ResourceKey<Level> dimension, ChunkPos pos) {
        return cache.remove(chunkKey(dimension, pos.x, pos.z));
    }

    public static void saveAll() {
        cache.values().forEach(ChunkData::saveIfDirty);
    }
}
