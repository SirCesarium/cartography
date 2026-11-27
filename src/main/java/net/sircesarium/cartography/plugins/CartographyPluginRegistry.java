package net.sircesarium.cartography.plugins;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.api.ICartographyPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds all registered {@link ICartographyPlugin} instances and drives their
 * initialisation once the mod-loading lifecycle permits it.
 */
public final class CartographyPluginRegistry {

    private static final List<ICartographyPlugin> PLUGINS = new ArrayList<>();

    private CartographyPluginRegistry() {}

    /** Register a plugin. Must be called before {@link #initAll}. */
    public static void register(ICartographyPlugin plugin) {
        PLUGINS.add(plugin);
    }

    /** Read-only view of the registered plugins (mostly for diagnostics). */
    public static List<ICartographyPlugin> getPlugins() {
        return Collections.unmodifiableList(PLUGINS);
    }

    /**
     * Initialise every registered plugin whose required mod is present.
     * Called once from {@code FMLCommonSetupEvent} in {@link Cartography}.
     */
    public static void initAll(IEventBus neoForgeEventBus) {
        for (ICartographyPlugin plugin : PLUGINS) {
            String required = plugin.requiredMod();

            if (!required.isEmpty() && !ModList.get().isLoaded(required)) {
                Cartography.LOGGER.debug("Skipping plugin '{}': mod '{}' not loaded", plugin.id(), required);
                continue;
            }

            try {
                plugin.onInit(neoForgeEventBus);
                Cartography.LOGGER.info("Plugin '{}' initialised", plugin.id());
            } catch (Exception e) {
                Cartography.LOGGER.error("Plugin '{}' failed to initialise", plugin.id(), e);
            }
        }
    }
}
