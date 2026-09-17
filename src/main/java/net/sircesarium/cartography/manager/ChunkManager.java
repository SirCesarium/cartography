package net.sircesarium.cartography.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.sircesarium.cartography.data.storage.ChunkData;
import net.sircesarium.cartography.util.ChunkScanner;

import java.util.concurrent.ConcurrentHashMap;

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

    public static void scanAndSave(ServerLevel level, ChunkPos pos) {
        ChunkData data = getOrCreate(level, pos);
        ChunkAccess chunk = level.getChunk(pos.x, pos.z);

        ChunkScanner.scanChunk(chunk, data);

        data.saveIfDirty();
    }

    public static void scanColumnAndSave(ServerLevel level, BlockPos blockPos) {
        ChunkPos pos = new ChunkPos(blockPos);
        ChunkData data = getOrCreate(level, pos);
        ChunkAccess chunk = level.getChunk(pos.x, pos.z);

        ChunkScanner.scanSingleColumn(chunk, blockPos.getX(), blockPos.getZ(), data);

        data.saveIfDirty();
    }
}
