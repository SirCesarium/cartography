package net.sircesarium.cartography.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.sircesarium.cartography.Cartography;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class NBTData {
    private final Path dataPath;
    private final String fileName;

    protected NBTData(ServerLevel level, String fileName) {
        this.fileName = fileName;

        this.dataPath = level.getServer()
                .getWorldPath(LevelResource.ROOT)
                .resolve("cartography");
    }

    protected NBTData(Path dataPath, String fileName) {
        this.fileName = fileName;
        this.dataPath = dataPath;
    }

    protected abstract CompoundTag toNBT();

    protected abstract void fromNBT(CompoundTag tag);

    public void save() {
        try {
            Path file = dataPath.resolve(fileName);

            if (!file.getParent().toFile().mkdirs() && !file.getParent().toFile().exists()) {
                Cartography.LOGGER.error("Failed to create directory: {}", file.getParent());
            }

            NbtIo.writeCompressed(toNBT(), file);
        } catch (IOException e) {
            Cartography.LOGGER.error("Failed to save {}", fileName, e);
        }
    }

    public void load() {
        Path file = dataPath.resolve(fileName);

        if (!Files.exists(file)) return;

        try {
            fromNBT(NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap()));
        } catch (IOException e) {
            Cartography.LOGGER.error("Failed to load {}", fileName, e);
        }
    }
}
