package net.sircesarium.cartography.client.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.client.pin.PinStateManager;

@EventBusSubscriber(modid = Cartography.MODID, value = Dist.CLIENT)
public class PinStateEvents {

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new PinStateManager());
    }
}
