package net.sircesarium.cartography.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.manager.ChunkManager;
import net.sircesarium.cartography.manager.SubLevelManager;
import net.sircesarium.cartography.manager.WaypointManager;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Cartography.MODID)
public class ServerLifecycleEvents {

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onServerStopping(ServerStoppingEvent event) {
        ChunkManager.saveAll();
        SubLevelManager.saveAll();
        WaypointManager.saveAll();
    }
}
