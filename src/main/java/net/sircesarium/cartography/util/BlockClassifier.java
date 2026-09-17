package net.sircesarium.cartography.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public final class BlockClassifier {

    private BlockClassifier() {
    }

    public static BlockType classify(BlockState state, FluidState fluid) {
        if (state.isAir()) return BlockType.AIR;
        if (fluid.getType() == Fluids.WATER || fluid.getType() == Fluids.FLOWING_WATER) return BlockType.WATER;
        if (state.canOcclude()) return BlockType.TERRAIN;
        if (isPlant(state)) return BlockType.VEGETATION;
        return BlockType.DECORATIONS;
    }

    public static boolean isPlant(BlockState state) {
        return state.is(BlockTags.FLOWERS)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.CROPS)
                || state.is(BlockTags.SMALL_FLOWERS)
                || state.is(BlockTags.TALL_FLOWERS);
    }

    public static String getRegistryName(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    public static String getRegistryName(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid).toString();
    }
}
