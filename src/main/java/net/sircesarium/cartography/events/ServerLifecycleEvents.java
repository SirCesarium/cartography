package net.sircesarium.cartography.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.sircesarium.cartography.manager.ChunkManager;
import net.sircesarium.cartography.manager.SubLevelManager;
import net.sircesarium.cartography.manager.WaypointManager;

@EventBusSubscriber(modid = "cartography")
public class ServerLifecycleEvents {

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ChunkManager.saveAll();
        SubLevelManager.saveAll();
        WaypointManager.saveAll();
    }
}
