package net.sircesarium.cartography.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.api.event.CartographyPinEvent;
import net.sircesarium.cartography.data.pin.Pin;
import net.sircesarium.cartography.data.storage.PinData;

public class PinManager {
    private static final ConcurrentHashMap<UUID, Pin> pinsByAuthor = new ConcurrentHashMap<>();
    private static PinData storage;

    public static void load(ServerLevel overworld) {
        pinsByAuthor.clear();

        storage = new PinData(overworld);
        storage.load();

        for (Pin pin : storage.getPins()) {
            pinsByAuthor.put(pin.author(), pin);
        }

        Cartography.LOGGER.info("Loaded {} pins from disk", pinsByAuthor.size());
    }

    public static void saveAll() {
        if (storage == null) return;

        storage.saveIfDirty();
    }

    public static void saveAndClear() {
        saveAll();
        pinsByAuthor.clear();
        storage = null;
    }

    public static boolean addPin(Pin pin) {
        Pin old = pinsByAuthor.put(pin.author(), pin);

        if (old != null) {
            Cartography.LOGGER.info("Replaced pin for {}", pin.author());
            NeoForge.EVENT_BUS.post(new CartographyPinEvent.Removed(old));
        }

        syncStorage();
        NeoForge.EVENT_BUS.post(new CartographyPinEvent.Added(pin));
        Cartography.LOGGER.info("Added pin {} at {} in {}", pin.typeId(), pin.pos(), pin.dimension().location());

        return true;
    }

    public static boolean removePin(UUID id) {
        for (var entry : pinsByAuthor.entrySet()) {
            if (entry.getValue().id().equals(id)) {
                Pin removed = pinsByAuthor.remove(entry.getKey());

                if (removed != null) {
                    syncStorage();
                    NeoForge.EVENT_BUS.post(new CartographyPinEvent.Removed(removed));
                }

                return removed != null;
            }
        }

        return false;
    }

    public static boolean removePin(BlockPos pos, ResourceKey<Level> dimension) {
        for (var entry : pinsByAuthor.entrySet()) {
            Pin pin = entry.getValue();

            if (pin.pos().equals(pos) && pin.dimension().equals(dimension)) {
                Pin removed = pinsByAuthor.remove(entry.getKey());

                if (removed != null) {
                    syncStorage();
                    NeoForge.EVENT_BUS.post(new CartographyPinEvent.Removed(removed));
                }

                return removed != null;
            }
        }

        return false;
    }

    public static Pin getPin(BlockPos pos, ResourceKey<Level> dimension) {
        for (Pin pin : pinsByAuthor.values()) {
            if (pin.pos().equals(pos) && pin.dimension().equals(dimension)) {
                return pin;
            }
        }

        return null;
    }

    public static Pin getPinByAuthor(UUID author) {
        return pinsByAuthor.get(author);
    }

    public static List<Pin> getPins(ResourceKey<Level> dimension) {
        List<Pin> result = new ArrayList<>();

        for (Pin pin : pinsByAuthor.values()) {
            if (pin.dimension().equals(dimension)) {
                result.add(pin);
            }
        }

        return result;
    }

    public static List<Pin> getAllPins() {
        return List.copyOf(pinsByAuthor.values());
    }

    public static int sweepExpired() {
        int removedCount = 0;

        for (var entry : pinsByAuthor.entrySet()) {
            if (entry.getValue().isExpired()) {
                Pin expired = pinsByAuthor.remove(entry.getKey());

                if (expired != null) {
                    removedCount++;
                    NeoForge.EVENT_BUS.post(new CartographyPinEvent.Expired(expired));
                }
            }
        }

        if (removedCount > 0) {
            syncStorage();
        }

        return removedCount;
    }

    public static int size() {
        return pinsByAuthor.size();
    }

    private static void syncStorage() {
        if (storage == null) return;

        storage.setPins(List.copyOf(pinsByAuthor.values()));
    }
}
