package net.sircesarium.cartography.plugins.builtin.create;

import com.simibubi.create.content.trains.graph.TrackEdge;

import net.minecraft.world.phys.Vec3;

public record CreateTrackEdgeInfo(
        int node1NetId,
        int node2NetId,
        double length,
        boolean isTurn,
        boolean isInterDimensional,
        String trackMaterial
) {
    public Vec3 getPosition(TrackEdge edge, double t) {
        return edge.getPosition(null, t);
    }

    public static CreateTrackEdgeInfo from(TrackEdge edge) {
        return new CreateTrackEdgeInfo(
                edge.node1.getNetId(),
                edge.node2.getNetId(),
                edge.getLength(),
                edge.isTurn(),
                edge.isInterDimensional(),
                edge.getTrackMaterial() != null ? edge.getTrackMaterial().toString() : ""
        );
    }
}
