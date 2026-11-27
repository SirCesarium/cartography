package net.sircesarium.cartography.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.sircesarium.cartography.data.storage.ChunkData;

public class ChunkScanner {

    public static void scanChunk(ServerLevel level, ChunkAccess chunk, ChunkData data) {
        int topY = (chunk.getHighestFilledSectionIndex() + 1) * 16 - 1;
        int minSectionY = chunk.getMinSection();
        int chunkMinX = chunk.getPos().getMinBlockX();
        int chunkMinZ = chunk.getPos().getMinBlockZ();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkMinX + x;
                int worldZ = chunkMinZ + z;

                scanColumn(level, chunk, x, z, worldX, worldZ, topY, minSectionY, data);
            }
        }
    }

    public static void scanSingleColumn(ServerLevel level, ChunkAccess chunk, int worldX, int worldZ, ChunkData data) {
        int localX = worldX - chunk.getPos().getMinBlockX();
        int localZ = worldZ - chunk.getPos().getMinBlockZ();
        int topY = (chunk.getHighestFilledSectionIndex() + 1) * 16 - 1;
        int minSectionY = chunk.getMinSection();

        scanColumn(level, chunk, localX, localZ, worldX, worldZ, topY, minSectionY, data);
    }

    private static void scanColumn(ServerLevel level, ChunkAccess chunk, int localX, int localZ,
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

            BlockType type = BlockClassifier.classify(state, fluid);

            switch (type) {
                case TERRAIN -> {
                    terrainBlock = BlockClassifier.getRegistryName(state.getBlock());
                    terrainY = y;
                }
                case WATER -> {
                    waterDepth++;

                    if (waterBlock == null) {
                        waterBlock = BlockClassifier.getRegistryName(fluid.getType());
                        waterY = y;
                    }
                }
                case VEGETATION -> {
                    if (vegetationBlock == null) {
                        vegetationBlock = BlockClassifier.getRegistryName(state.getBlock());
                        vegetationY = y;
                    }
                }
                case DECORATIONS -> {
                    if (decorationsBlock == null) {
                        decorationsBlock = BlockClassifier.getRegistryName(state.getBlock());
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
            int blockLight = level.getBrightness(LightLayer.BLOCK, new BlockPos(worldX, terrainY + 1, worldZ));
            changed |= data.getLight().setLight(localX, localZ, blockLight);
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

    private static String getBiomeName(ChunkAccess chunk, int worldX, int y, int worldZ) {
        Holder<Biome> biomeHolder = chunk.getNoiseBiome(worldX, y, worldZ);

        ResourceKey<Biome> key = biomeHolder.unwrapKey().orElse(null);

        return key != null ? key.location().toString() : "unknown";
    }

    /** Scans only the light at a single column. Used for delayed re-scan after block events. */
    public static void scanLightColumn(ServerLevel level, ChunkAccess chunk, int localX, int localZ,
                                       int worldX, int worldZ, ChunkData data) {
        int topY = (chunk.getHighestFilledSectionIndex() + 1) * 16 - 1;
        int minSectionY = chunk.getMinSection();

        int terrainY = 0;

        for (int y = topY; y >= minSectionY * 16; y--) {
            BlockState state = chunk.getBlockState(new BlockPos(worldX, y, worldZ));

            if (state.canOcclude()) {
                terrainY = y;
                break;
            }
        }

        if (terrainY != 0) {
            int blockLight = level.getBrightness(LightLayer.BLOCK, new BlockPos(worldX, terrainY + 1, worldZ));

            if (data.getLight().setLight(localX, localZ, blockLight)) {
                data.markDirty();
            }
        }
    }
}
