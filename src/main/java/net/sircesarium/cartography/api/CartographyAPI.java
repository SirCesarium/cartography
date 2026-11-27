package net.sircesarium.cartography.api;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.sircesarium.cartography.data.Waypoint;
import net.sircesarium.cartography.data.pin.Pin;
import net.sircesarium.cartography.data.pin.context.PinContextRegistry;
import net.sircesarium.cartography.data.pin.context.PinDataContext;
import net.sircesarium.cartography.manager.PinManager;
import net.sircesarium.cartography.manager.WaypointManager;
import net.sircesarium.cartography.plugins.CartographyPluginRegistry;

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

    /**
     * Queue a plugin factory that is only instantiated if {@code requiredMod}
     * is loaded (or always when {@code requiredMod} is empty).
     */
    public static void registerPlugin(String requiredMod, Supplier<ICartographyPlugin> factory) {
        CartographyPluginRegistry.register(requiredMod, factory);
    }

    // ── Pin context registration ──────────────────────────────────────────

    /** Register a custom {@link PinDataContext} so the pin system can resolve it. */
    public static void registerContext(PinDataContext context) {
        PinContextRegistry.register(context);
    }

    // ── Pin management ────────────────────────────────────────────────────

    /** Add or replace the author's active pin. */
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

    /** Add a waypoint in {@code level} and mark storage dirty. */
    public static void addWaypoint(
            ServerLevel level,
            BlockPos pos,
            String name,
            Integer color,
            ResourceLocation icon,
            UUID author,
            Long expiresAt
    ) {
        WaypointManager.getOrCreate(level)
                .addWaypoint(pos, level.dimension(), name, color, icon, author, expiresAt);
    }

    /** Add a waypoint without a custom color in {@code level} and mark storage dirty. */
    public static void addWaypoint(
            ServerLevel level,
            BlockPos pos,
            String name,
            ResourceLocation icon,
            UUID author,
            Long expiresAt
    ) {
        addWaypoint(level, pos, name, null, icon, author, expiresAt);
    }

    /** Remove a waypoint by id from {@code level}. Returns {@code true} if it existed. */
    public static boolean removeWaypoint(ServerLevel level, UUID id) {
        return WaypointManager.getOrCreate(level).removeWaypoint(id);
    }

    /** Return all non-expired waypoints stored for {@code level}. */
    public static List<Waypoint> getWaypoints(ServerLevel level) {
        return WaypointManager.getOrCreate(level).getWaypoints();
    }

    /** Persist any dirty waypoint changes for {@code level}. */
    public static void saveWaypoints(ServerLevel level) {
        WaypointManager.getOrCreate(level).saveIfDirty();
    }
}
