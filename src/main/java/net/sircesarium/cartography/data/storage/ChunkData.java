package net.sircesarium.cartography.data.storage;

import java.nio.file.Path;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.sircesarium.cartography.data.layers.Layer;
import net.sircesarium.cartography.data.layers.WaterLayer;
import net.sircesarium.cartography.util.NBTData;

@Getter
public class ChunkData extends NBTData {
    private final int chunkX;
    private final int chunkZ;

    private Layer terrain;
    private WaterLayer water;
    private Layer vegetation;
    private Layer decorations;
    private Layer biome;

    public ChunkData(ServerLevel level, int chunkX, int chunkZ) {
        super(level, "chunks/" + level.dimension().location().getPath() + "/c." + chunkX + "." + chunkZ + ".dat");
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        initLayers();
    }

    public ChunkData(Path basePath, String fileName, int chunkX, int chunkZ) {
        super(basePath, fileName);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        initLayers();
    }

    private void initLayers() {
        this.terrain = new Layer();
        this.water = new WaterLayer();
        this.vegetation = new Layer();
        this.decorations = new Layer();
        this.biome = new Layer();
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
        this.terrain = Layer.load(tag.getCompound("terrain"));
        this.water = WaterLayer.load(tag.getCompound("water"));
        this.vegetation = Layer.load(tag.getCompound("vegetation"));
        this.decorations = Layer.load(tag.getCompound("decorations"));
        this.biome = Layer.load(tag.getCompound("biome"));
    }
}
