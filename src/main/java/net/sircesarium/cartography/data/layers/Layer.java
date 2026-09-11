package net.sircesarium.cartography.data.layers;

import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Layer {
    public static final int CHUNK_SIZE = 16;
    protected static final int BLOCKS_PER_CHUNK = CHUNK_SIZE * CHUNK_SIZE;

    protected List<String> palette;
    protected byte[] blocks;
    protected int[] yCoordinates;

    protected Layer() {
        this.palette = new ArrayList<>();
        this.blocks = new byte[BLOCKS_PER_CHUNK];
        this.yCoordinates = null;
    }

    public int getBlockIndex(int localX, int localZ) {
        if (localX < 0 || localX >= CHUNK_SIZE || localZ < 0 || localZ >= CHUNK_SIZE) {
            throw new IndexOutOfBoundsException("Block coords out of range: " + localX + "," + localZ);
        }
        return localZ * CHUNK_SIZE + localX;
    }

    public int getPaletteIndex(String blockName) {
        int index = palette.indexOf(blockName);

        if (index == -1) {
            palette.add(blockName);

            return palette.size();
        }

        return index + 1;
    }

    public boolean setBlock(int localX, int localZ, String blockName) {
        return setBlock(localX, localZ, blockName, 0);
    }

    public boolean setBlock(int localX, int localZ, String blockName, int y) {
        int idx = getBlockIndex(localX, localZ);
        int newIdx = getPaletteIndex(blockName);

        boolean changed = blocks[idx] != (byte) newIdx;

        blocks[idx] = (byte) newIdx;

        if (yCoordinates == null && y != 0) {
            yCoordinates = new int[BLOCKS_PER_CHUNK];
        }

        if (yCoordinates != null && yCoordinates[idx] != y) {
            yCoordinates[idx] = y;
            changed = true;
        }

        return changed;
    }

    public String getBlock(int localX, int localZ) {
        int paletteIdx = blocks[getBlockIndex(localX, localZ)] & 0xFF;
        if (paletteIdx == 0) return null;

        return palette.get(paletteIdx - 1);
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag paletteTag = new ListTag();

        for (String name : palette) {
            paletteTag.add(StringTag.valueOf(name));
        }

        tag.put("palette", paletteTag);
        tag.putByteArray("blocks", blocks);

        if (yCoordinates != null) {
            tag.putIntArray("y", yCoordinates);
        }

        return tag;
    }

    public void fromNBT(CompoundTag tag) {
        this.palette = new ArrayList<>();

        ListTag paletteTag = tag.getList("palette", Tag.TAG_STRING);

        for (int i = 0; i < paletteTag.size(); i++) {
            palette.add(paletteTag.getString(i));
        }

        byte[] loaded = tag.getByteArray("blocks");

        this.blocks = loaded.length == BLOCKS_PER_CHUNK ? loaded : new byte[BLOCKS_PER_CHUNK];

        if (tag.contains("y")) {
            int[] loadedY = tag.getIntArray("y");
            this.yCoordinates = loadedY.length == BLOCKS_PER_CHUNK ? loadedY : null;
        } else {
            this.yCoordinates = null;
        }
    }
}
