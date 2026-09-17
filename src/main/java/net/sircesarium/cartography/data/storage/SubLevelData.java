package net.sircesarium.cartography.data.storage;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.helpers.FileIOHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

@Getter
public class SubLevelData {
    private final UUID uuid;
    private final Path basePath;

    private double posX, posY, posZ;
    private double scaleX, scaleY, scaleZ;

    private final Map<Long, ChunkData> chunks = new HashMap<>();
    private boolean poseDirty;

    public SubLevelData(ServerLevel level, UUID uuid) {
        this.uuid = uuid;
        this.basePath = level.getServer()
                .getWorldPath(LevelResource.ROOT)
                .resolve("cartography")
                .resolve("sublevels")
                .resolve(uuid.toString());

        this.poseDirty = false;
    }

    public void updatePose(Vector3d position, Vector3d scale) {
        boolean changed = this.posX != position.x() || this.posY != position.y() || this.posZ != position.z()
                || this.scaleX != scale.x() || this.scaleY != scale.y() || this.scaleZ != scale.z();

        this.posX = position.x();
        this.posY = position.y();
        this.posZ = position.z();
        this.scaleX = scale.x();
        this.scaleY = scale.y();
        this.scaleZ = scale.z();
        this.poseDirty = changed;
    }

    public ChunkData getOrCreateChunk(int chunkX, int chunkZ) {
        long key = chunkKey(chunkX, chunkZ);

        return chunks.computeIfAbsent(key, k -> {
            Path chunkDir = basePath.resolve("chunks");
            String fileName = "c." + chunkX + "." + chunkZ + ".dat";
            ChunkData data = new ChunkData(chunkDir, fileName, chunkX, chunkZ);

            data.load();

            return data;
        });
    }

    public void savePose() {
        try {
            FileIOHelper.ensureDirectory(basePath);
            FileIOHelper.writeNbt(getCompoundTag(), basePath.resolve("meta.dat"));
            poseDirty = false;
        } catch (Exception e) {
            Cartography.LOGGER.error("Failed to save sublevel meta for {}", uuid, e);
        }
    }

    private @NotNull CompoundTag getCompoundTag() {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("uuid", uuid);
        tag.putDouble("posX", posX);
        tag.putDouble("posY", posY);
        tag.putDouble("posZ", posZ);
        tag.putDouble("scaleX", scaleX);
        tag.putDouble("scaleY", scaleY);
        tag.putDouble("scaleZ", scaleZ);
        return tag;
    }

    public void loadPose() {
        Path file = basePath.resolve("meta.dat");

        if (!FileIOHelper.exists(file)) return;

        try {
            CompoundTag tag = FileIOHelper.readNbt(file);

            posX = tag.getDouble("posX");
            posY = tag.getDouble("posY");
            posZ = tag.getDouble("posZ");
            scaleX = tag.getDouble("scaleX");
            scaleY = tag.getDouble("scaleY");
            scaleZ = tag.getDouble("scaleZ");
        } catch (Exception e) {
            Cartography.LOGGER.error("Failed to load sublevel meta for {}", uuid, e);
        }
    }

    public void saveAllChunks() {
        for (ChunkData chunk : chunks.values()) {
            chunk.saveIfDirty();
        }
    }

    static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }
}
