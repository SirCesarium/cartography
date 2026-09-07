package net.sircesarium.cartography.data.layers;

import net.minecraft.nbt.CompoundTag;

public class VegetationLayer extends Layer {

    public VegetationLayer() {
        super();
    }

    public static VegetationLayer load(CompoundTag tag) {
        VegetationLayer layer = new VegetationLayer();

        layer.fromNBT(tag);

        return layer;
    }
}
