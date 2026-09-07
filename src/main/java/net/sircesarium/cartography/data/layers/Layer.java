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

    protected Layer() {
        this.palette = new ArrayList<>();
        this.blocks = new byte[BLOCKS_PER_CHUNK];
    }

    public int getBlockIndex(int localX, int localZ) {
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
        int idx = getBlockIndex(localX, localZ);
        int newIdx = getPaletteIndex(blockName);

        if (blocks[idx] == (byte) newIdx) return false;

        blocks[idx] = (byte) newIdx;

        return true;
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
    }
}
