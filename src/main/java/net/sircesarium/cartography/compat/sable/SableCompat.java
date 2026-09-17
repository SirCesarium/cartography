package net.sircesarium.cartography.compat.sable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.sircesarium.cartography.Cartography;
import dev.ryanhcode.sable.neoforge.event.ForgeSableSubLevelContainerReadyEvent;

public class SableCompat {

    private static final List<SableSubLevelObserver> observers = new CopyOnWriteArrayList<>();

    public static void init() {
        Cartography.LOGGER.info("Sable detected, registering sub-level compat");
        IEventBus neoBus = NeoForge.EVENT_BUS;

        neoBus.addListener(SableCompat::onContainerReady);
        neoBus.addListener(SableCompat::onServerStopping);
    }

    private static void onContainerReady(ForgeSableSubLevelContainerReadyEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            Cartography.LOGGER.info("Sable container ready, registering observer");

            SableSubLevelObserver observer = new SableSubLevelObserver(serverLevel);

            observers.add(observer);

            event.getContainer().addObserver(observer);
        }
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        for (SableSubLevelObserver observer : observers) {
            observer.close();
        }

        observers.clear();
    }
}
