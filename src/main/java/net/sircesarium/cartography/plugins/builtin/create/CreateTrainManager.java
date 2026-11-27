package net.sircesarium.cartography.plugins.builtin.create;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.station.GlobalStation;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class CreateTrainManager {

    private static final CopyOnWriteArrayList<Consumer<CreateTrainInfo>> trainAddedListeners = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Consumer<CreateTrainInfo>> trainRemovedListeners = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Consumer<CreateTrainInfo>> trainMovedListeners = new CopyOnWriteArrayList<>();

    private static final CopyOnWriteArrayList<Consumer<CreateStationInfo>> stationAddedListeners = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<Consumer<CreateStationInfo>> stationRemovedListeners = new CopyOnWriteArrayList<>();

    private static Map<UUID, CreateTrainInfo> trainSnapshot = new HashMap<>();
    private static Map<String, CreateStationInfo> stationSnapshot = new HashMap<>();
    private static List<CreateTrackGraphInfo> graphSnapshot = new ArrayList<>();
    private static int lastGraphChecksum = -1;

    public static void onTrainAdded(Consumer<CreateTrainInfo> listener) {
        trainAddedListeners.add(listener);
    }

    public static void onTrainRemoved(Consumer<CreateTrainInfo> listener) {
        trainRemovedListeners.add(listener);
    }

    public static void onTrainMoved(Consumer<CreateTrainInfo> listener) {
        trainMovedListeners.add(listener);
    }

    public static void onStationAdded(Consumer<CreateStationInfo> listener) {
        stationAddedListeners.add(listener);
    }

    public static void onStationRemoved(Consumer<CreateStationInfo> listener) {
        stationRemovedListeners.add(listener);
    }

    public static Collection<CreateTrainInfo> getTrains() {
        return Collections.unmodifiableCollection(trainSnapshot.values());
    }

    public static Optional<CreateTrainInfo> getTrain(UUID id) {
        return Optional.ofNullable(trainSnapshot.get(id));
    }

    public static List<CreateTrainInfo> getTrainsInDimension(ResourceKey<Level> dimension) {
        List<CreateTrainInfo> result = new ArrayList<>();

        for (CreateTrainInfo info : trainSnapshot.values()) {
            if (info.primaryDimension().equals(dimension))
                result.add(info);
        }

        return result;
    }

    public static Collection<CreateStationInfo> getStations() {
        return Collections.unmodifiableCollection(stationSnapshot.values());
    }

    public static List<CreateStationInfo> getStationsInDimension(ResourceKey<Level> dimension) {
        List<CreateStationInfo> result = new ArrayList<>();

        for (CreateStationInfo info : stationSnapshot.values()) {
            if (info.dimension().equals(dimension))
                result.add(info);
        }

        return result;
    }

    public static List<CreateTrackGraphInfo> getTrackGraphs() {
        return Collections.unmodifiableList(graphSnapshot);
    }

    public static void tick(MinecraftServer server) {
        tickTrains(server);
        tickStations();
        tickTrackGraphs();
    }

    private static void tickTrains(MinecraftServer server) {
        Map<UUID, CreateTrainInfo> newSnapshot = new HashMap<>();

        for (ServerLevel level : server.getAllLevels()) {
            for (Train train : Create.RAILWAYS.trains.values()) {
                if (train.invalid || train.derailed)
                    continue;

                Optional<BlockPos> pos = train.getPositionInDimension(level.dimension());

                if (pos.isEmpty()) continue;

                if (!newSnapshot.containsKey(train.id))
                    newSnapshot.put(train.id, CreateTrainInfo.from(train, level.dimension()));
            }
        }

        for (Map.Entry<UUID, CreateTrainInfo> entry : newSnapshot.entrySet()) {
            UUID id = entry.getKey();
            CreateTrainInfo info = entry.getValue();

            if (!trainSnapshot.containsKey(id)) {
                fireListeners(trainAddedListeners, info);
            } else {
                CreateTrainInfo old = trainSnapshot.get(id);

                if (!old.position().equals(info.position()) || !old.primaryDimension().equals(info.primaryDimension()))
                    fireListeners(trainMovedListeners, info);
            }
        }

        for (Map.Entry<UUID, CreateTrainInfo> entry : trainSnapshot.entrySet()) {
            if (!newSnapshot.containsKey(entry.getKey()))
                fireListeners(trainRemovedListeners, entry.getValue());
        }

        trainSnapshot = newSnapshot;
    }

    private static void tickStations() {
        Map<String, CreateStationInfo> newSnapshot = new HashMap<>();

        for (TrackGraph graph : Create.RAILWAYS.trackNetworks.values()) {
            for (GlobalStation station : graph.getPoints(EdgePointType.STATION)) {
                newSnapshot.put(station.name, CreateStationInfo.from(station));
            }
        }

        for (Map.Entry<String, CreateStationInfo> entry : newSnapshot.entrySet()) {
            if (!stationSnapshot.containsKey(entry.getKey()))
                fireListeners(stationAddedListeners, entry.getValue());
        }

        for (Map.Entry<String, CreateStationInfo> entry : stationSnapshot.entrySet()) {
            if (!newSnapshot.containsKey(entry.getKey()))
                fireListeners(stationRemovedListeners, entry.getValue());
        }

        stationSnapshot = newSnapshot;
    }

    private static void tickTrackGraphs() {
        int version = Create.RAILWAYS.version;

        if (version == lastGraphChecksum) return;

        lastGraphChecksum = version;

        List<CreateTrackGraphInfo> newSnapshot = new ArrayList<>();

        for (TrackGraph graph : Create.RAILWAYS.trackNetworks.values()) {
            if (!graph.isEmpty())
                newSnapshot.add(CreateTrackGraphInfo.from(graph));
        }

        graphSnapshot = newSnapshot;
    }

    public static void reset() {
        for (CreateTrainInfo info : trainSnapshot.values())
            fireListeners(trainRemovedListeners, info);

        for (CreateStationInfo info : stationSnapshot.values())
            fireListeners(stationRemovedListeners, info);

        trainSnapshot = new HashMap<>();
        stationSnapshot = new HashMap<>();
        graphSnapshot = new ArrayList<>();
        lastGraphChecksum = -1;
        trainAddedListeners.clear();
        trainRemovedListeners.clear();
        trainMovedListeners.clear();
        stationAddedListeners.clear();
        stationRemovedListeners.clear();
    }

    private static <T> void fireListeners(CopyOnWriteArrayList<Consumer<T>> listeners, T info) {
        for (Consumer<T> listener : listeners) {
            try {
                listener.accept(info);
            } catch (Exception ignored) {
            }
        }
    }
}
