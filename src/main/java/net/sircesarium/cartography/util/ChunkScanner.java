package net.sircesarium.cartography.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.sircesarium.cartography.data.storage.ChunkData;

public class ChunkScanner {

    public static void scanChunk(ChunkAccess chunk, ChunkData data) {
        int topY = (chunk.getHighestFilledSectionIndex() + 1) * 16 - 1;
        int minSectionY = chunk.getMinSection();
        int chunkMinX = chunk.getPos().getMinBlockX();
        int chunkMinZ = chunk.getPos().getMinBlockZ();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkMinX + x;
                int worldZ = chunkMinZ + z;

                scanColumn(chunk, x, z, worldX, worldZ, topY, minSectionY, data);
            }
        }
    }

    public static void scanSingleColumn(ChunkAccess chunk, int worldX, int worldZ, ChunkData data) {
        int localX = worldX - chunk.getPos().getMinBlockX();
        int localZ = worldZ - chunk.getPos().getMinBlockZ();
        int topY = (chunk.getHighestFilledSectionIndex() + 1) * 16 - 1;
        int minSectionY = chunk.getMinSection();

        scanColumn(chunk, localX, localZ, worldX, worldZ, topY, minSectionY, data);
    }

    private static void scanColumn(ChunkAccess chunk, int localX, int localZ,
                                   int worldX, int worldZ, int topY, int minSectionY,
                                   ChunkData data) {
        String terrainBlock = null;
        String vegetationBlock = null;
        String waterBlock = null;
        String decorationsBlock = null;

        int waterDepth = 0;
        int terrainY = 0;
        int waterY = 0;
        int vegetationY = 0;
        int decorationsY = 0;

        for (int y = topY; y >= minSectionY * 16; y--) {
            BlockPos pos = new BlockPos(worldX, y, worldZ);
            BlockState state = chunk.getBlockState(pos);
            FluidState fluid = state.getFluidState();

            BlockType type = classify(state, fluid);

            switch (type) {
                case TERRAIN -> {
                    terrainBlock = getRegistryName(state.getBlock());
                    terrainY = y;
                }
                case WATER -> {
                    waterDepth++;

                    if (waterBlock == null) {
                        waterBlock = getRegistryName(fluid.getType());
                        waterY = y;
                    }
                }
                case VEGETATION -> {
                    if (vegetationBlock == null) {
                        vegetationBlock = getRegistryName(state.getBlock());
                        vegetationY = y;
                    }
                }
                case DECORATIONS -> {
                    if (decorationsBlock == null) {
                        decorationsBlock = getRegistryName(state.getBlock());
                        decorationsY = y;
                    }
                }
                case AIR -> {
                }
            }

            if (terrainBlock != null) break;
        }

        String biomeName = getBiomeName(chunk, worldX, topY, worldZ);

        boolean changed = false;

        if (terrainBlock != null) {
            changed |= data.getTerrain().setBlock(localX, localZ, terrainBlock, terrainY);
        }

        if (vegetationBlock != null) {
            changed |= data.getVegetation().setBlock(localX, localZ, vegetationBlock, vegetationY);
        }

        if (waterBlock != null && waterDepth > 0) {
            changed |= data.getWater().setWater(localX, localZ, waterBlock, waterDepth, waterY);
        }

        if (decorationsBlock != null) {
            changed |= data.getDecorations().setBlock(localX, localZ, decorationsBlock, decorationsY);
        }

        changed |= data.getBiome().setBlock(localX, localZ, biomeName);

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

    private static String getBiomeName(ChunkAccess chunk, int worldX, int y, int worldZ) {
        Holder<Biome> biomeHolder = chunk.getNoiseBiome(worldX, y, worldZ);

        ResourceKey<Biome> key = biomeHolder.unwrapKey().orElse(null);

        return key != null ? key.location().toString() : "unknown";
    }
}
