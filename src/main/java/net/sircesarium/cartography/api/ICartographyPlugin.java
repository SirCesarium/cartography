package net.sircesarium.cartography.api;

import net.neoforged.bus.api.IEventBus;

/**
 * Entry point for a Cartography addon. Implement this interface and register
 * the instance via {@link CartographyAPI#registerPlugin} during your mod's
 * constructor (or during {@code FMLCommonSetupEvent} at the latest).
 *
 * <p>Built-in plugins live under {@code plugins/builtin/}; third-party plugins
 * depend on the cartography API JAR and register themselves the same way.
 */
public interface ICartographyPlugin {

    /** Unique snake_case identifier for this plugin, e.g. {@code "create"}. */
    String id();

    /**
     * NeoForge mod-id that must be present for this plugin to initialise.
     * Return an empty string to always initialise regardless of loaded mods.
     */
    String requiredMod();

    /**
     * Called once, after all {@code @Mod} constructors have run, on the
     * NeoForge event bus. Register your event listeners here.
     */
    void onInit(IEventBus neoForgeEventBus);
}
