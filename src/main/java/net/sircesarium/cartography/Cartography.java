package net.sircesarium.cartography;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Cartography.MODID)
public class Cartography {
    public static final String MODID = "cartography";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Cartography(IEventBus modEventBus) {
    }
}
