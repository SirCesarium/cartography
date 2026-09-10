package net.sircesarium.cartography.manager;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.sircesarium.cartography.data.storage.ChunkData;

public class ChunkManager {
    private static final ConcurrentHashMap<Long, ChunkData> cache = new ConcurrentHashMap<>();

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    public static ChunkData getOrCreate(ServerLevel level, ChunkPos pos) {
        return cache.computeIfAbsent(chunkKey(pos.x, pos.z), k -> {
            ChunkData data = new ChunkData(level, pos.x, pos.z);
            data.load();
            return data;
        });
    }

    public static ChunkData remove(ChunkPos pos) {
        return cache.remove(chunkKey(pos.x, pos.z));
    }

    public static void saveAll() {
        cache.values().forEach(ChunkData::saveIfDirty);
    }
}
