package net.sircesarium.cartography.data.storage;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.sircesarium.cartography.data.Waypoint;
import net.sircesarium.cartography.util.NBTData;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unused")
@Getter
public class WaypointData extends NBTData {
    private final List<Waypoint> waypoints;

    public WaypointData(ServerLevel level) {
        super(level, "waypoints.dat");
        this.waypoints = new CopyOnWriteArrayList<>();
    }

    public List<Waypoint> getWaypoints() {
        if (waypoints.removeIf(Waypoint::isExpired)) {
            markDirty();
        }
        return waypoints;
    }

    public void addWaypoint(BlockPos pos, ResourceKey<Level> dimension, String name, Integer color, ResourceLocation icon, UUID author, Long expiresAt) {
        waypoints.add(new Waypoint(pos, dimension, name, color, icon, author, expiresAt));
        markDirty();
    }

    public void addWaypoint(UUID id, BlockPos pos, ResourceKey<Level> dimension, String name, Integer color, ResourceLocation icon, UUID author, Long expiresAt) {
        if (waypoints.stream().anyMatch(wp -> wp.getId().equals(id))) return;

        waypoints.add(new Waypoint(id, pos, dimension, name, color, icon, author, expiresAt));
        markDirty();
    }

    public void updateWaypoint(Waypoint updated) {
        for (int i = 0; i < waypoints.size(); i++) {
            if (waypoints.get(i).getId().equals(updated.getId())) {
                waypoints.set(i, updated);
                markDirty();
                return;
            }
        }
    }

    public boolean hasWaypointAt(BlockPos pos, ResourceKey<Level> dimension) {
        return waypoints.stream()
                .anyMatch(wp -> wp.getPos().equals(pos) && wp.getDimension().equals(dimension));
    }

    public boolean hasWaypointAt(BlockPos pos) {
        return waypoints.stream()
                .anyMatch(wp -> wp.getPos().equals(pos));
    }

    public boolean hasWaypointWithId(UUID id) {
        return waypoints.stream()
                .anyMatch(wp -> wp.getId().equals(id));
    }

    @Override
    protected CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();

        for (Waypoint wp : waypoints) {
            list.add(wp.toNBT());
        }

        tag.put("waypoints", list);

        return tag;
    }

    @Override
    protected void fromNBT(CompoundTag tag) {
        waypoints.clear();

        ListTag list = tag.getList("waypoints", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            waypoints.add(Waypoint.fromNBT(list.getCompound(i)));
        }
    }
}
