package net.sircesarium.cartography.compat.create;

import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record CreateTrackNodeInfo(
        int netId,
        Vec3 position,
        Vec3 normal,
        ResourceKey<Level> dimension
) {
    public static CreateTrackNodeInfo from(TrackNode node) {
        TrackNodeLocation loc = node.getLocation();
        return new CreateTrackNodeInfo(
                node.getNetId(),
                loc.getLocation(),
                node.getNormal(),
                loc.getDimension()
        );
    }
}
