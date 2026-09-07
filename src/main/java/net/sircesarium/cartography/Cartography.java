package net.sircesarium.cartography;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;

@SuppressWarnings("unused")
@Mod(Cartography.MODID)
public class Cartography {
    public static final String MODID = "cartography";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Cartography(IEventBus modEventBus, ModContainer modContainer) {
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