package net.sircesarium.cartography.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.helpers.FileIOHelper;

import java.nio.file.Path;

public abstract class NBTData {
    private final Path dataPath;
    private final String fileName;
    private boolean dirty;

    protected NBTData(ServerLevel level, String fileName) {
        this.fileName = fileName;
        this.dirty = false;

        this.dataPath = level.getServer()
                .getWorldPath(LevelResource.ROOT)
                .resolve("cartography");
    }

    protected NBTData(Path dataPath, String fileName) {
        this.fileName = fileName;
        this.dataPath = dataPath;
        this.dirty = false;
    }

    protected abstract CompoundTag toNBT();

    protected abstract void fromNBT(CompoundTag tag);

    public void markDirty() {
        this.dirty = true;
    }

    public void saveIfDirty() {
        if (dirty) {
            save();
            dirty = false;
        }
    }

    public void save() {
        try {
            Path file = dataPath.resolve(fileName);
            FileIOHelper.writeNbt(toNBT(), file);
        } catch (Exception e) {
            Cartography.LOGGER.error("Failed to save {}", fileName, e);
        }
    }

    public void load() {
        Path file = dataPath.resolve(fileName);

        if (!FileIOHelper.exists(file)) return;

        try {
            fromNBT(FileIOHelper.readNbt(file));
        } catch (Exception e) {
            Cartography.LOGGER.error("Failed to load {}", fileName, e);
        }
    }
}
