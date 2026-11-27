package net.sircesarium.cartography;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.sircesarium.cartography.api.CartographyAPI;
import net.sircesarium.cartography.config.CartographyServerConfig;
import net.sircesarium.cartography.data.pin.PinRegistry;
import net.sircesarium.cartography.data.pin.context.BlockContext;
import net.sircesarium.cartography.data.pin.context.ItemContext;
import net.sircesarium.cartography.data.pin.context.MobContext;
import net.sircesarium.cartography.data.pin.context.PlayerContext;
import net.sircesarium.cartography.plugins.CartographyPluginRegistry;
import net.sircesarium.cartography.plugins.builtin.create.CreatePlugin;
import net.sircesarium.cartography.plugins.builtin.sable.SablePlugin;

@Mod(Cartography.MODID)
public class Cartography {
    public static final String MODID = "cartography";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Cartography(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, CartographyServerConfig.SPEC);
        PinRegistry.register(modContainer.getEventBus());

        CartographyAPI.registerContext(new MobContext());
        CartographyAPI.registerContext(new ItemContext());
        CartographyAPI.registerContext(new BlockContext());
        CartographyAPI.registerContext(new PlayerContext());

        CartographyAPI.registerPlugin(new SablePlugin());
        CartographyAPI.registerPlugin(new CreatePlugin());

        // Defer plugin init until after all @Mod constructors have registered their plugins
        modContainer.getEventBus().addListener(this::setup);
    }

    private void setup(FMLCommonSetupEvent event) {
        CartographyPluginRegistry.initAll(NeoForge.EVENT_BUS);
    }
}
