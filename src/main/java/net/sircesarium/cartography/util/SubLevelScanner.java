package net.sircesarium.cartography.util;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;

import net.sircesarium.cartography.data.storage.ChunkData;
import net.sircesarium.cartography.data.storage.SubLevelData;

public class SubLevelScanner {

    public static void scanSubLevel(SubLevel subLevel, SubLevelData subLevelData) {
        LevelPlot plot = subLevel.getPlot();
        Collection<PlotChunkHolder> loadedChunks = plot.getLoadedChunks();

        for (PlotChunkHolder holder : loadedChunks) {
            LevelChunk chunk = holder.getChunk();

            int chunkX = chunk.getPos().x;
            int chunkZ = chunk.getPos().z;

            ChunkData data = subLevelData.getOrCreateChunk(chunkX, chunkZ);

            scanChunk(chunk, data);
        }
    }

    public static void scanChunk(LevelChunk chunk, ChunkData data) {
        int topY = (chunk.getHighestFilledSectionIndex() + 1) * 16 - 1;
        int minSectionY = chunk.getMinSection();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                scanColumn(chunk, x, z, topY, minSectionY, data);
            }
        }
    }

    private static void scanColumn(LevelChunk chunk, int localX, int localZ,
                                   int topY, int minSectionY, ChunkData data) {
        String terrainBlock = null;
        String vegetationBlock = null;
        String waterBlock = null;
        String decorationsBlock = null;
        int waterDepth = 0;

        for (int y = topY; y >= minSectionY * 16; y--) {
            BlockPos pos = new BlockPos(localX, y, localZ);
            BlockState state = chunk.getBlockState(pos);
            FluidState fluid = state.getFluidState();

            BlockType type = classify(state, fluid);

            switch (type) {
                case TERRAIN -> terrainBlock = getRegistryName(state.getBlock());
                case WATER -> {
                    waterDepth++;
                    if (waterBlock == null) {
                        waterBlock = getRegistryName(fluid.getType());
                    }
                }
                case VEGETATION -> {
                    if (vegetationBlock == null) {
                        vegetationBlock = getRegistryName(state.getBlock());
                    }
                }
                case DECORATIONS -> {
                    if (decorationsBlock == null) {
                        decorationsBlock = getRegistryName(state.getBlock());
                    }
                }
                case AIR -> {
                }
            }

            if (terrainBlock != null) break;
        }

        boolean changed = false;
        if (terrainBlock != null) {
            changed |= data.getTerrain().setBlock(localX, localZ, terrainBlock);
        }
        if (vegetationBlock != null) {
            changed |= data.getVegetation().setBlock(localX, localZ, vegetationBlock);
        }
        if (waterBlock != null && waterDepth > 0) {
            changed |= data.getWater().setWater(localX, localZ, waterBlock, waterDepth);
        }
        if (decorationsBlock != null) {
            changed |= data.getDecorations().setBlock(localX, localZ, decorationsBlock);
        }
        if (changed) {
            data.markDirty();
        }
    }

    static BlockType classify(BlockState state, FluidState fluid) {
        if (state.isAir()) return BlockType.AIR;
        if (fluid.getType() == Fluids.WATER || fluid.getType() == Fluids.FLOWING_WATER) return BlockType.WATER;
        if (state.canOcclude()) return BlockType.TERRAIN;
        if (isPlant(state)) return BlockType.VEGETATION;

        return BlockType.DECORATIONS;
    }

    private static boolean isPlant(BlockState state) {
        return state.is(BlockTags.FLOWERS)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.CROPS)
                || state.is(BlockTags.SMALL_FLOWERS)
                || state.is(BlockTags.TALL_FLOWERS);
    }

    private static String getRegistryName(Object obj) {
        ResourceLocation key;

        if (obj instanceof Block block) {
            key = BuiltInRegistries.BLOCK.getKey(block);
        } else if (obj instanceof Fluid fluid) {
            key = BuiltInRegistries.FLUID.getKey(fluid);
        } else {
            return "unknown";
        }

        return key.toString();
    }
}
