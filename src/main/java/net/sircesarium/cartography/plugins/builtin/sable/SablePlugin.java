package net.sircesarium.cartography.plugins.builtin.sable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dev.ryanhcode.sable.neoforge.event.ForgeSableSubLevelContainerReadyEvent;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.api.ICartographyPlugin;

public class SablePlugin implements ICartographyPlugin {

    private final List<SableSubLevelObserver> observers = new CopyOnWriteArrayList<>();

    @Override
    public String id() {
        return "sable";
    }

    @Override
    public String requiredMod() {
        return "sable";
    }

    @Override
    public void onInit(IEventBus neoForgeEventBus) {
        Cartography.LOGGER.info("Sable detected, registering sub-level compat");

        neoForgeEventBus.addListener(this::onContainerReady);
        neoForgeEventBus.addListener(this::onServerStopping);
    }

    private void onContainerReady(ForgeSableSubLevelContainerReadyEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        Cartography.LOGGER.info("Sable container ready, registering observer");

        SableSubLevelObserver observer = new SableSubLevelObserver(serverLevel);

        observers.add(observer);
        event.getContainer().addObserver(observer);
    }

    private void onServerStopping(ServerStoppingEvent event) {
        for (SableSubLevelObserver observer : observers) {
            observer.close();
        }

        observers.clear();
    }
}
