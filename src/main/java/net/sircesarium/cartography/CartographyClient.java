package net.sircesarium.cartography;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.sircesarium.cartography.config.CartographyClientConfig;

@Mod(value = Cartography.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Cartography.MODID, value = Dist.CLIENT)
public class CartographyClient {

    public CartographyClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> {
            CartographyClientConfig.HANDLER.load();
            return CartographyClientConfig.HANDLER.generateGui().generateScreen(parent);
        });
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
    }
}
