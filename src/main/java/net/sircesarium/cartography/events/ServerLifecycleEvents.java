package net.sircesarium.cartography.events;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel)) return;

        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            int removed = PinManager.sweepExpired();
            if (removed > 0)
                Cartography.LOGGER.info("Sweeped {} expired pins", removed);
        }
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ChunkManager.saveAll();
        SubLevelManager.saveAll();
        WaypointManager.saveAll();
    }
}
