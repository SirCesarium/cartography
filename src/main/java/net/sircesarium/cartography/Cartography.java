package net.sircesarium.cartography;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.sircesarium.cartography.config.CartographyServerConfig;
import net.sircesarium.cartography.data.pin.PinRegistry;

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

        if (ModList.get().isLoaded("sable")) {
            initCompat("net.sircesarium.cartography.compat.sable.SableCompat", "Sable");
        }

        if (ModList.get().isLoaded("create")) {
            initCompat("net.sircesarium.cartography.compat.create.CreateCompat", "Create");
        }
    }

    private static void initCompat(String className, String modName) {
        try {
            Class.forName(className)
                    .getMethod("init")
                    .invoke(null);

            LOGGER.info("{} compat initialized", modName);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize {} compat", modName, e);
        }
    }
}
