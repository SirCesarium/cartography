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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class WaypointData extends NBTData {
    private final List<Waypoint> waypoints;
    private boolean dirty;
    private Runnable onChange;

    public WaypointData(ServerLevel level) {
        super(level, "waypoints.dat");

        this.waypoints = new ArrayList<>();
        this.dirty = false;
    }

    public WaypointData(Path dataPath, String fileName) {
        super(dataPath, fileName);

        this.waypoints = new ArrayList<>();
        this.dirty = false;
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public List<Waypoint> getWaypoints() {
        if (waypoints.removeIf(Waypoint::isExpired)) {
            dirty = true;
            fireChange();
        }
        return waypoints;
    }

    public void addWaypoint(BlockPos pos, ResourceKey<Level> dimension, String name, Integer color, ResourceLocation icon, UUID author, Long expiresAt) {
        waypoints.add(new Waypoint(pos, dimension, name, color, icon, author, expiresAt));

        dirty = true;
        fireChange();
    }

    public void addWaypoint(UUID id, BlockPos pos, ResourceKey<Level> dimension, String name, Integer color, ResourceLocation icon, UUID author, Long expiresAt) {
        waypoints.add(new Waypoint(id, pos, dimension, name, color, icon, author, expiresAt));

        dirty = true;
        fireChange();
    }

    public boolean removeWaypoint(int index) {
        if (index < 0 || index >= waypoints.size()) return false;

        waypoints.remove(index);
        dirty = true;
        fireChange();

        return true;
    }

    public boolean removeWaypointById(UUID id) {
        if (waypoints.removeIf(wp -> wp.getId().equals(id))) {
            dirty = true;
            fireChange();
            return true;
        }
        return false;
    }

    public boolean updateWaypoint(Waypoint updated) {
        for (int i = 0; i < waypoints.size(); i++) {
            if (waypoints.get(i).getId().equals(updated.getId())) {
                waypoints.set(i, updated);
                dirty = true;
                fireChange();
                return true;
            }
        }
        return false;
    }

    public void removeExpired() {
        if (waypoints.removeIf(Waypoint::isExpired)) {
            dirty = true;
            fireChange();
        }
    }

    public void markDirty() {
        this.dirty = true;
    }

    private void fireChange() {
        if (onChange != null) {
            onChange.run();
        }
    }

    public void saveIfDirty() {
        if (dirty) {
            save();

            dirty = false;
        }
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
