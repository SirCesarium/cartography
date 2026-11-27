package net.sircesarium.cartography.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.manager.ChunkManager;
import net.sircesarium.cartography.manager.PinManager;
import net.sircesarium.cartography.manager.SubLevelManager;
import net.sircesarium.cartography.manager.WaypointManager;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Cartography.MODID)
public class ServerLifecycleEvents {
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        tickCounter = 0;
        PinManager.load(event.getServer().overworld());
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;

        tickCounter++;

        if (tickCounter >= 20) {
            tickCounter = 0;
            int removed = PinManager.sweepExpired();

            if (removed > 0) {
                Cartography.LOGGER.info("Sweeped {} expired pins", removed);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ChunkManager.saveAll();
        ChunkManager.clear();
        SubLevelManager.saveAll();
        SubLevelManager.clear();
        WaypointManager.saveAll();
        WaypointManager.clear();
        PinManager.saveAndClear();
    }
}
