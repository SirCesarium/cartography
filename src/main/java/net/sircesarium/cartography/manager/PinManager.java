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
    private static final ConcurrentHashMap<UUID, Pin> pinsByAuthor = new ConcurrentHashMap<>();

    public static boolean addPin(Pin pin) {
        Pin old = pinsByAuthor.put(pin.author(), pin);

        if (old != null) {
            Cartography.LOGGER.info("Replaced pin for {}", pin.author());
        }

        Cartography.LOGGER.info("Added pin {} at {} in {}", pin.typeId(), pin.pos(), pin.dimension().location());
        return true;
    }

    public static boolean removePin(UUID id) {
        for (var entry : pinsByAuthor.entrySet()) {
            if (entry.getValue().id().equals(id)) {
                pinsByAuthor.remove(entry.getKey());
                return true;
            }
        }

        return false;
    }

    public static boolean removePin(BlockPos pos, ResourceKey<Level> dimension) {
        for (var entry : pinsByAuthor.entrySet()) {
            Pin pin = entry.getValue();

            if (pin.pos().equals(pos) && pin.dimension().equals(dimension)) {
                pinsByAuthor.remove(entry.getKey());
                return true;
            }
        }

        return false;
    }

    public static Pin getPin(BlockPos pos, ResourceKey<Level> dimension) {
        for (Pin pin : pinsByAuthor.values()) {
            if (pin.pos().equals(pos) && pin.dimension().equals(dimension))
                return pin;
        }

        return null;
    }

    public static Pin getPinByAuthor(UUID author) {
        return pinsByAuthor.get(author);
    }

    public static List<Pin> getPins(ResourceKey<Level> dimension) {
        List<Pin> result = new ArrayList<>();

        for (Pin pin : pinsByAuthor.values()) {
            if (pin.dimension().equals(dimension))
                result.add(pin);
        }

        return result;
    }

    public static List<Pin> getAllPins() {
        return List.copyOf(pinsByAuthor.values());
    }

    public static int sweepExpired() {
        int removed = 0;

        for (var entry : pinsByAuthor.entrySet()) {
            if (entry.getValue().isExpired()) {
                pinsByAuthor.remove(entry.getKey());
                removed++;
            }
        }

        return removed;
    }

    public static int size() {
        return pinsByAuthor.size();
    }
}
