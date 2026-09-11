package net.sircesarium.cartography.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.sircesarium.cartography.data.storage.ChunkData;
import net.sircesarium.cartography.manager.ChunkManager;
import net.sircesarium.cartography.util.ChunkScanner;

@EventBusSubscriber(modid = "cartography")
public class ChunkEvents {

    private static void scanAndSave(ServerLevel level, ChunkPos pos) {
        ChunkData data = ChunkManager.getOrCreate(level, pos);
        ChunkAccess chunk = level.getChunk(pos.x, pos.z);

        ChunkScanner.scanChunk(chunk, data);

        data.saveIfDirty();
    }

    private static void scanColumnAndSave(ServerLevel level, BlockPos blockPos) {
        ChunkPos pos = new ChunkPos(blockPos);
        ChunkData data = ChunkManager.getOrCreate(level, pos);
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

        ServerLevel level = (ServerLevel) event.getLevel();
        ChunkPos pos = event.getChunk().getPos();
        ChunkData data = ChunkManager.remove(level.dimension(), pos);

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
