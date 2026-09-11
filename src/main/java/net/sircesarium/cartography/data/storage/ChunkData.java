package net.sircesarium.cartography.data.storage;

import java.nio.file.Path;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.sircesarium.cartography.data.layers.BiomeLayer;
import net.sircesarium.cartography.data.layers.DecorationsLayer;
import net.sircesarium.cartography.data.layers.TerrainLayer;
import net.sircesarium.cartography.data.layers.VegetationLayer;
import net.sircesarium.cartography.data.layers.WaterLayer;
import net.sircesarium.cartography.util.NBTData;

@Getter
public class ChunkData extends NBTData {
    private final int chunkX;
    private final int chunkZ;
    @Getter
    private boolean dirty;

    private TerrainLayer terrain;
    private WaterLayer water;
    private VegetationLayer vegetation;
    private DecorationsLayer decorations;
    private BiomeLayer biome;

    public ChunkData(ServerLevel level, int chunkX, int chunkZ) {
        super(level, "chunks/" + level.dimension().location().getPath() + "/c." + chunkX + "." + chunkZ + ".dat");
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.dirty = false;
        this.terrain = new TerrainLayer();
        this.water = new WaterLayer();
        this.vegetation = new VegetationLayer();
        this.decorations = new DecorationsLayer();
        this.biome = new BiomeLayer();
    }

    public ChunkData(Path basePath, String fileName, int chunkX, int chunkZ) {
        super(basePath, fileName);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.dirty = false;
        this.terrain = new TerrainLayer();
        this.water = new WaterLayer();
        this.vegetation = new VegetationLayer();
        this.decorations = new DecorationsLayer();
        this.biome = new BiomeLayer();
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

    public void loadFromNBT(CompoundTag tag) {
        fromNBT(tag);
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("terrain", terrain.toNBT());
        tag.put("water", water.toNBT());
        tag.put("vegetation", vegetation.toNBT());
        tag.put("decorations", decorations.toNBT());
        tag.put("biome", biome.toNBT());
        return tag;
    }

    @Override
    protected void fromNBT(CompoundTag tag) {
        this.terrain = TerrainLayer.load(tag.getCompound("terrain"));
        this.water = WaterLayer.load(tag.getCompound("water"));
        this.vegetation = VegetationLayer.load(tag.getCompound("vegetation"));
        this.decorations = DecorationsLayer.load(tag.getCompound("decorations"));
        this.biome = BiomeLayer.load(tag.getCompound("biome"));
    }
}
