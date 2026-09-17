package net.sircesarium.cartography.events;

import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.data.storage.ChunkData;
import net.sircesarium.cartography.helpers.LevelHelper;
import net.sircesarium.cartography.manager.ChunkManager;

@SuppressWarnings({"unused", "resource"})
@EventBusSubscriber(modid = Cartography.MODID)
public class ChunkEvents {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        var level = LevelHelper.serverLevel(event.getLevel()).orElse(null);
        if (level == null) return;

        ChunkManager.scanAndSave(level, event.getChunk().getPos());
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        var level = LevelHelper.serverLevel(event.getLevel()).orElse(null);
        if (level == null) return;

        ChunkPos pos = event.getChunk().getPos();
        ChunkData data = ChunkManager.remove(level.dimension(), pos);

        if (data != null) {
            data.saveIfDirty();
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        var level = LevelHelper.serverLevel(event.getLevel()).orElse(null);
        if (level == null) return;

        ChunkManager.scanColumnAndSave(level, event.getPos());
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        var level = LevelHelper.serverLevel(event.getLevel()).orElse(null);
        if (level == null) return;

        ChunkManager.scanColumnAndSave(level, event.getPos());
    }
}
