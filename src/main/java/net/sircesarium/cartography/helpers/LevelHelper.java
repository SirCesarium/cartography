package net.sircesarium.cartography.helpers;

import java.util.Optional;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

public final class LevelHelper {

    private LevelHelper() {
    }

    public static Optional<ServerLevel> serverLevel(LevelAccessor level) {
        if (level.isClientSide()) return Optional.empty();

        return Optional.of((ServerLevel) level);
    }
}
