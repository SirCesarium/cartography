package net.sircesarium.cartography.data.pin.context;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

@PinContext("block")
public class BlockContext implements PinDataContext {

    @Override
    public String getContextName() {
        return "block";
    }

    @PinGetter(value = "resourceLocation", type = "string", description = "Block registry name")
    public String resourceLocation(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    @PinGetter(value = "block", type = "string", description = "Block name")
    public String block(BlockState state) {
        return state.getBlock().toString();
    }

    @PinGetter(value = "isAir", type = "boolean", description = "Whether the block is air")
    public boolean isAir(BlockState state) {
        return state.isAir();
    }

    @PinGetter(value = "isRedstone", type = "boolean", description = "Whether the block is a redstone signal source")
    public boolean isRedstone(BlockState state) {
        return state.isSignalSource();
    }

    @PinGetter(value = "requiresTool", type = "boolean", description = "Whether the block requires a tool to drop")
    public boolean requiresTool(BlockState state) {
        return state.requiresCorrectToolForDrops();
    }
}
