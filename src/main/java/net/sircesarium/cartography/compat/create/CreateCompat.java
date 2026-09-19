package net.sircesarium.cartography.compat.create;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.sircesarium.cartography.Cartography;

public class CreateCompat {

    private static int tickCounter = 0;

    public static void init() {
        Cartography.LOGGER.info("Create detected, registering train compat");
        IEventBus neoBus = NeoForge.EVENT_BUS;

        neoBus.addListener(CreateCompat::onLevelTick);
        neoBus.addListener(CreateCompat::onServerStopping);
    }

    private static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;

        if (serverLevel.dimension() != Level.OVERWORLD)
            return;

        tickCounter++;
        if (tickCounter < 20)
            return;

        tickCounter = 0;

        MinecraftServer server = serverLevel.getServer();
        CreateTrainManager.tick(server);
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        CreateTrainManager.reset();
    }
}
