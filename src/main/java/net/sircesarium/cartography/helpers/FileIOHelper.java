package net.sircesarium.cartography.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.sircesarium.cartography.Cartography;

@SuppressWarnings("unused")
public final class FileIOHelper {

    private FileIOHelper() {
    }

    public static boolean ensureDirectory(Path dir) {
        if (!dir.toFile().mkdirs() && !dir.toFile().exists()) {
            Cartography.LOGGER.error("Failed to create directory: {}", dir);
            return false;
        }
        return true;
    }

    public static void writeNbt(CompoundTag tag, Path file) throws IOException {
        ensureDirectory(file.getParent());
        NbtIo.writeCompressed(tag, file);
    }

    public static CompoundTag readNbt(Path file) throws IOException {
        return NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
    }

    public static boolean exists(Path file) {
        return Files.exists(file);
    }

    public static void deleteRecursive(Path dir) {
        try {
            if (Files.exists(dir)) {
                try (var stream = Files.walk(dir)) {
                    stream.sorted(java.util.Comparator.reverseOrder())
                            .forEach(path -> {
                                try {
                                    Files.deleteIfExists(path);
                                } catch (IOException e) {
                                    Cartography.LOGGER.error("Failed to delete {}", path, e);
                                }
                            });
                }
            }
        } catch (IOException e) {
            Cartography.LOGGER.error("Failed to delete directory {}", dir, e);
        }
    }
}
