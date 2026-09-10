package net.sircesarium.cartography.data.layers;

import net.minecraft.nbt.CompoundTag;

public class BiomeLayer extends Layer {

    public BiomeLayer() {
        super();
    }

    public static BiomeLayer load(CompoundTag tag) {
        BiomeLayer layer = new BiomeLayer();

        layer.fromNBT(tag);

        return layer;
    }
}
