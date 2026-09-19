package net.sircesarium.cartography.compat.create;

import java.util.*;

import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;

import net.createmod.catnip.theme.Color;

public record CreateTrackGraphInfo(
        UUID graphId,
        Color color,
        List<CreateTrackNodeInfo> nodes,
        List<CreateTrackEdgeInfo> edges
) {
    public static CreateTrackGraphInfo from(TrackGraph graph) {
        List<CreateTrackNodeInfo> nodes = new ArrayList<>();
        List<CreateTrackEdgeInfo> edges = new ArrayList<>();

        for (TrackNode node : graph.getNodes().stream()
                .map(graph::locateNode)
                .filter(Objects::nonNull)
                .toList()) {
            nodes.add(CreateTrackNodeInfo.from(node));
        }

        for (TrackNode node : graph.getNodes().stream()
                .map(graph::locateNode)
                .filter(Objects::nonNull)
                .toList()) {
            Map<TrackNode, TrackEdge> connections = graph.getConnectionsFrom(node);

            if (connections == null)
                continue;

            for (Map.Entry<TrackNode, TrackEdge> entry : connections.entrySet()) {
                TrackNode other = entry.getKey();
                TrackEdge edge = entry.getValue();

                if (node.getNetId() < other.getNetId())
                    edges.add(CreateTrackEdgeInfo.from(edge));
            }
        }

        return new CreateTrackGraphInfo(
                graph.id,
                graph.color,
                nodes,
                edges
        );
    }
}
