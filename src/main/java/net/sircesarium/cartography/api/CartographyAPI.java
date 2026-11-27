package net.sircesarium.cartography.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.sircesarium.cartography.data.pin.Pin;
import net.sircesarium.cartography.data.pin.context.PinContextRegistry;
import net.sircesarium.cartography.data.pin.context.PinDataContext;
import net.sircesarium.cartography.manager.PinManager;
import net.sircesarium.cartography.manager.WaypointManager;
import net.sircesarium.cartography.plugins.CartographyPluginRegistry;

import java.util.List;
import java.util.UUID;

/**
 * Public surface for interacting with Cartography at runtime.
 *
 * <p>All methods delegate straight to the relevant internal manager or
 * registry — this class exists solely as a stable, versioned API boundary
 * so third-party addons do not need to import internal packages.
 */
public final class CartographyAPI {

    private CartographyAPI() {}

    // ── Plugin registration ───────────────────────────────────────────────

    /** Queue a plugin for initialisation during {@code FMLCommonSetupEvent}. */
    public static void registerPlugin(ICartographyPlugin plugin) {
        CartographyPluginRegistry.register(plugin);
    }

    // ── Pin context registration ──────────────────────────────────────────

    /** Register a custom {@link PinDataContext} so the pin system can resolve it. */
    public static void registerContext(PinDataContext context) {
        PinContextRegistry.register(context);
    }

    // ── Pin management ────────────────────────────────────────────────────

    /**
     * Add a pin. Returns {@code false} if a pin for the same author already
     * exists (it is replaced and this returns {@code true}).
     */
    public static boolean addPin(Pin pin) {
        return PinManager.addPin(pin);
    }

    /** Remove the pin with the given id. Returns {@code true} if it existed. */
    public static boolean removePin(UUID id) {
        return PinManager.removePin(id);
    }

    /** All pins currently active in the given dimension. */
    public static List<Pin> getPins(ResourceKey<Level> dimension) {
        return PinManager.getPins(dimension);
    }

    // ── Waypoint management ───────────────────────────────────────────────

    /**
     * Persist all in-memory waypoint data for every loaded level.
     * Plugins should call this if they add waypoints outside the normal
     * command flow.
     */
    public static void saveWaypoints(ServerLevel level) {
        WaypointManager.getOrCreate(level).saveIfDirty();
    }
}
