package net.sircesarium.cartography.plugins.builtin.create;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.api.ICartographyPlugin;

public class CreatePlugin implements ICartographyPlugin {

    private int tickCounter = 0;

    @Override
    public String id() {
        return "create";
    }

    @Override
    public String requiredMod() {
        return "create";
    }

    @Override
    public void onInit(IEventBus neoForgeEventBus) {
        Cartography.LOGGER.info("Create detected, registering train compat");

        neoForgeEventBus.addListener(this::onLevelTick);
        neoForgeEventBus.addListener(this::onServerStopping);
    }

    private void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        if (serverLevel.dimension() != Level.OVERWORLD) return;

        tickCounter++;

        if (tickCounter < 20) return;

        tickCounter = 0;

        MinecraftServer server = serverLevel.getServer();

        CreateTrainManager.tick(server);
    }

    private void onServerStopping(ServerStoppingEvent event) {
        CreateTrainManager.reset();
    }
}
