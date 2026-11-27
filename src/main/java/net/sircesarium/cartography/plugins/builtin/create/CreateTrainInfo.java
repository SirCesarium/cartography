package net.sircesarium.cartography.plugins.builtin.create;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record CreateTrainInfo(
        UUID uuid,
        String name,
        double speed,
        boolean invalid,
        boolean derailed,
        @Nullable UUID currentStation,
        List<ResourceKey<Level>> dimensions,
        ResourceKey<Level> primaryDimension,
        BlockPos position,
        @Nullable UUID ownerId,
        int carriageCount,
        int fuelTicks,
        String iconId,
        String scheduleState,
        int currentScheduleEntry,
        boolean schedulePaused
) {
    public boolean isMoving() {
        return Math.abs(speed) > 0.01;
    }

    public boolean hasSchedule() {
        return !scheduleState.isEmpty();
    }

    public boolean hasOwner() {
        return ownerId != null;
    }

    public static CreateTrainInfo from(com.simibubi.create.content.trains.entity.Train train, ResourceKey<Level> dimension) {
        Optional<BlockPos> pos = train.getPositionInDimension(dimension);

        String iconId = train.icon != null && train.icon.getId() != null
                ? train.icon.getId().toString()
                : "";

        String scheduleState = "";
        int currentEntry = -1;
        boolean schedulePaused = false;

        if (train.runtime != null) {
            scheduleState = train.runtime.state != null ? train.runtime.state.name() : "";
            currentEntry = train.runtime.currentEntry;
            schedulePaused = train.runtime.paused;
        }

        return new CreateTrainInfo(
                train.id,
                train.name.getString(),
                train.speed,
                train.invalid,
                train.derailed,
                train.currentStation,
                train.getPresentDimensions(),
                dimension,
                pos.orElse(BlockPos.ZERO),
                train.owner,
                train.carriages.size(),
                train.fuelTicks,
                iconId,
                scheduleState,
                currentEntry,
                schedulePaused
        );
    }
}
