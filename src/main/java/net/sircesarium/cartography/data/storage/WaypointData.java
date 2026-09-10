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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Getter
public class WaypointData extends NBTData {
    private final List<Waypoint> waypoints;
    private boolean dirty;

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

    public void addWaypoint(BlockPos pos, ResourceKey<Level> dimension, String name, Integer color, ResourceLocation icon, UUID author, Long expiresAt) {
        waypoints.add(new Waypoint(pos, dimension, name, color, icon, author, expiresAt));

        dirty = true;
    }

    public boolean removeWaypoint(int index) {
        if (index < 0 || index >= waypoints.size()) return false;

        waypoints.remove(index);
        dirty = true;

        return true;
    }

    public void removeExpired() {
        Iterator<Waypoint> it = waypoints.iterator();

        while (it.hasNext()) {
            if (it.next().isExpired()) {
                it.remove();

                dirty = true;
            }
        }
    }

    public void markDirty() {
        this.dirty = true;
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
