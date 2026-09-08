package net.sircesarium.cartography.events;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.sircesarium.cartography.data.storage.ChunkData;
import net.sircesarium.cartography.util.ChunkScanner;

@EventBusSubscriber(modid = "cartography")
public class ChunkEvents {

    private static final ConcurrentHashMap<Long, ChunkData> cache = new ConcurrentHashMap<>();

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    private static ChunkData getOrCreate(ServerLevel level, ChunkPos pos) {
        return cache.computeIfAbsent(chunkKey(pos.x, pos.z), k -> {
            ChunkData data = new ChunkData(level, pos.x, pos.z);

            data.load();

            return data;
        });
    }

    private static void scanAndSave(ServerLevel level, ChunkPos pos) {
        ChunkData data = getOrCreate(level, pos);
        ChunkAccess chunk = level.getChunk(pos.x, pos.z);

        ChunkScanner.scanChunk(chunk, data);

        data.saveIfDirty();
    }

    private static void scanColumnAndSave(ServerLevel level, BlockPos blockPos) {
        ChunkPos pos = new ChunkPos(blockPos);
        ChunkData data = getOrCreate(level, pos);
        ChunkAccess chunk = level.getChunk(pos.x, pos.z);

        ChunkScanner.scanSingleColumn(chunk, blockPos.getX(), blockPos.getZ(), data);

        data.saveIfDirty();
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel().isClientSide()) return;

        ServerLevel level = (ServerLevel) event.getLevel();

        scanAndSave(level, event.getChunk().getPos());
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel().isClientSide()) return;

        ChunkPos pos = event.getChunk().getPos();
        ChunkData data = cache.remove(chunkKey(pos.x, pos.z));

        if (data != null) {
            data.saveIfDirty();
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;

        scanColumnAndSave((ServerLevel) event.getLevel(), event.getPos());
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;

        scanColumnAndSave((ServerLevel) event.getLevel(), event.getPos());
    }
}
