package net.sircesarium.cartography;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.sircesarium.cartography.config.CartographyClientConfig;

@SuppressWarnings("unused")
@Mod(value = Cartography.MODID, dist = Dist.CLIENT)
public class CartographyClient {

    public CartographyClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (mc, parent) -> {
            CartographyClientConfig.HANDLER.load();

            return CartographyClientConfig.HANDLER.generateGui().generateScreen(parent);
        });
    }
}
