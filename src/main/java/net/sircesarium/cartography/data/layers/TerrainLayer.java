package net.sircesarium.cartography.data.layers;

import net.minecraft.nbt.CompoundTag;

public class TerrainLayer extends Layer {

    public TerrainLayer() {
        super();
    }

    public static TerrainLayer load(CompoundTag tag) {
        TerrainLayer layer = new TerrainLayer();

        layer.fromNBT(tag);

        return layer;
    }
}
