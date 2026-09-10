package net.sircesarium.cartography.data.layers;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;

@Getter
public class WaterLayer extends Layer {
    private byte[] depth;

    public WaterLayer() {
        super();
        this.depth = new byte[BLOCKS_PER_CHUNK];
    }

    public boolean setWater(int localX, int localZ, String fluidName, int waterDepth) {
        return setWater(localX, localZ, fluidName, waterDepth, 0);
    }

    public boolean setWater(int localX, int localZ, String fluidName, int waterDepth, int y) {
        boolean changed = setBlock(localX, localZ, fluidName, y);
        int idx = getBlockIndex(localX, localZ);
        byte newDepth = (byte) Math.min(waterDepth, 255);

        if (depth[idx] != newDepth) {
            depth[idx] = newDepth;
            changed = true;
        }

        return changed;
    }

    public int getWaterDepth(int localX, int localZ) {
        return depth[getBlockIndex(localX, localZ)] & 0xFF;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = super.toNBT();

        tag.putByteArray("depth", depth);

        return tag;
    }

    public void fromNBTWithDepth(CompoundTag tag) {
        super.fromNBT(tag);
        byte[] loaded = tag.getByteArray("depth");

        this.depth = loaded.length == BLOCKS_PER_CHUNK ? loaded : new byte[BLOCKS_PER_CHUNK];
    }

    public static WaterLayer load(CompoundTag tag) {
        WaterLayer layer = new WaterLayer();

        layer.fromNBTWithDepth(tag);

        return layer;
    }
}
