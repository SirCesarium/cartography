package net.sircesarium.cartography.data.layers;

import net.minecraft.nbt.CompoundTag;

import static net.sircesarium.cartography.data.layers.Layer.BLOCKS_PER_CHUNK;
import static net.sircesarium.cartography.data.layers.Layer.CHUNK_SIZE;

public class LightLayer {

    private final byte[] blockLight;

    public LightLayer() {
        this.blockLight = new byte[BLOCKS_PER_CHUNK];
    }

    private LightLayer(byte[] blockLight) {
        this.blockLight = blockLight;
    }

    public int getBlockIndex(int localX, int localZ) {
        if (localX < 0 || localX >= CHUNK_SIZE || localZ < 0 || localZ >= CHUNK_SIZE) {
            throw new IndexOutOfBoundsException("Block coords out of range: " + localX + "," + localZ);
        }

        return localZ * CHUNK_SIZE + localX;
    }

    /** Returns true if the value changed. Light is clamped to [0, 15]. */
    public boolean setLight(int localX, int localZ, int value) {
        int idx = getBlockIndex(localX, localZ);
        byte clamped = (byte) Math.clamp(value, 0, 15);

        if (blockLight[idx] == clamped) return false;

        blockLight[idx] = clamped;
        return true;
    }

    public int getBlockLight(int localX, int localZ) {
        return blockLight[getBlockIndex(localX, localZ)] & 0xFF;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putByteArray("blockLight", blockLight);
        return tag;
    }

    public void fromNBT(CompoundTag tag) {
        byte[] loaded = tag.getByteArray("blockLight");

        if (loaded.length == BLOCKS_PER_CHUNK) {
            System.arraycopy(loaded, 0, blockLight, 0, BLOCKS_PER_CHUNK);
        }
    }

    public static LightLayer load(CompoundTag tag) {
        LightLayer layer = new LightLayer();
        layer.fromNBT(tag);
        return layer;
    }
}
