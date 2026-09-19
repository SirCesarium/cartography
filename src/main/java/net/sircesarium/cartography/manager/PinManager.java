package net.sircesarium.cartography.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.data.pin.Pin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PinManager {
    private static final ConcurrentHashMap<ResourceKey<Level>, ConcurrentHashMap<BlockPos, Pin>> pins
            = new ConcurrentHashMap<>();

    public static boolean addPin(Pin pin) {
        ConcurrentHashMap<BlockPos, Pin> dim = pins.computeIfAbsent(pin.dimension(), k -> new ConcurrentHashMap<>());
        Pin existing = dim.putIfAbsent(pin.pos(), pin);

        if (existing != null) {
            Cartography.LOGGER.warn("Pin already exists at {} in {}", pin.pos(), pin.dimension().location());
            return false;
        }

        Cartography.LOGGER.info("Added pin {} at {} in {}", pin.typeId(), pin.pos(), pin.dimension().location());
        return true;
    }

    public static boolean removePin(UUID id) {
        for (var dim : pins.values()) {
            if (dim.values().removeIf(pin -> pin.id().equals(id)))
                return true;
        }

        return false;
    }

    public static boolean removePin(BlockPos pos, ResourceKey<Level> dimension) {
        ConcurrentHashMap<BlockPos, Pin> dim = pins.get(dimension);

        return dim != null && dim.remove(pos) != null;
    }

    public static Pin getPin(BlockPos pos, ResourceKey<Level> dimension) {
        ConcurrentHashMap<BlockPos, Pin> dim = pins.get(dimension);

        return dim != null ? dim.get(pos) : null;
    }

    public static List<Pin> getPins(ResourceKey<Level> dimension) {
        ConcurrentHashMap<BlockPos, Pin> dim = pins.get(dimension);

        return dim != null ? List.copyOf(dim.values()) : List.of();
    }

    public static List<Pin> getAllPins() {
        List<Pin> all = new ArrayList<>();

        for (var dim : pins.values()) {
            all.addAll(dim.values());
        }

        return all;
    }

    public static int sweepExpired() {
        int removed = 0;

        for (var dim : pins.values()) {
            if (dim.values().removeIf(Pin::isExpired))
                removed++;
        }

        return removed;
    }

    public static int size() {
        int total = 0;

        for (var dim : pins.values()) {
            total += dim.size();
        }

        return total;
    }
}
