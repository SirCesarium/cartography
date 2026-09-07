package net.sircesarium.cartography.data.layers;

import net.minecraft.nbt.CompoundTag;

public class DecorationsLayer extends Layer {

    public DecorationsLayer() {
        super();
    }

    public static DecorationsLayer load(CompoundTag tag) {
        DecorationsLayer layer = new DecorationsLayer();

        layer.fromNBT(tag);

        return layer;
    }
}
