package net.sircesarium.cartography;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.sircesarium.cartography.config.CartographyServerConfig;

@SuppressWarnings("unused")
@Mod(Cartography.MODID)
public class Cartography {
    public static final String MODID = "cartography";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Cartography(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, CartographyServerConfig.SPEC);

        if (ModList.get().isLoaded("sable")) {
            SableCompatInit.init();
        }
    }

    private static class SableCompatInit {
        static void init() {
            try {
                Class.forName("net.sircesarium.cartography.compat.sable.SableCompat")
                        .getMethod("init")
                        .invoke(null);
                LOGGER.info("Sable compat initialized");
            } catch (Exception e) {
                LOGGER.error("Failed to initialize Sable compat", e);
            }
        }
    }
}
