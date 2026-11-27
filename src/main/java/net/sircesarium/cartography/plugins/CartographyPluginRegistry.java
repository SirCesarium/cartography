package net.sircesarium.cartography.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.api.ICartographyPlugin;

/**
 * Holds all registered {@link ICartographyPlugin} entries and drives their
 * initialisation once the mod-loading lifecycle permits it.
 */
public final class CartographyPluginRegistry {

    private record Entry(String requiredMod, Supplier<ICartographyPlugin> factory) {}

    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static final List<ICartographyPlugin> INITIALIZED = new ArrayList<>();

    private CartographyPluginRegistry() {}

    /** Register an already-instantiated plugin. Must be called before {@link #initAll}. */
    public static void register(ICartographyPlugin plugin) {
        ENTRIES.add(new Entry(plugin.requiredMod(), () -> plugin));
    }

    /**
     * Register a plugin factory guarded by {@code requiredMod}.
     * The supplier is only invoked when {@code requiredMod} is loaded, so
     * optional-dependency classes are never classloaded otherwise.
     */
    public static void register(String requiredMod, Supplier<ICartographyPlugin> factory) {
        ENTRIES.add(new Entry(requiredMod, factory));
    }

    /** Read-only view of the plugins that were successfully initialised. */
    public static List<ICartographyPlugin> getPlugins() {
        return Collections.unmodifiableList(INITIALIZED);
    }

    /**
     * Initialise every registered plugin whose required mod is present.
     * Called once from {@code FMLCommonSetupEvent} in {@link Cartography}.
     */
    public static void initAll(IEventBus neoForgeEventBus) {
        for (Entry entry : ENTRIES) {
            String required = entry.requiredMod();

            if (!required.isEmpty() && !ModList.get().isLoaded(required)) {
                Cartography.LOGGER.debug("Skipping plugin: mod '{}' not loaded", required);
                continue;
            }

            try {
                ICartographyPlugin plugin = entry.factory().get();

                plugin.onInit(neoForgeEventBus);
                INITIALIZED.add(plugin);
                Cartography.LOGGER.info("Plugin '{}' initialised", plugin.id());
            } catch (Exception e) {
                Cartography.LOGGER.error("Plugin for mod '{}' failed to initialise", required, e);
            }
        }
    }
}
