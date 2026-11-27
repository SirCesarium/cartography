package net.sircesarium.cartography.plugins.builtin.create;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.trains.station.GlobalStation;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record CreateStationInfo(
        String name,
        BlockPos position,
        ResourceKey<Level> dimension,
        boolean assembling,
        @Nullable UUID nearestTrainId
) {
    public static CreateStationInfo from(GlobalStation station) {
        UUID nearestId = null;

        if (station.nearestTrain != null) {
            var train = station.nearestTrain.get();

            if (train != null)
                nearestId = train.id;
        }

        return new CreateStationInfo(
                station.name,
                station.getBlockEntityPos(),
                station.getBlockEntityDimension(),
                station.assembling,
                nearestId
        );
    }
}
