package net.sircesarium.cartography.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class MapColorHelper {

    private static final Map<String, int[]> COLOR_CACHE = new HashMap<>();
    private static final BlockPos DUMMY_POS = BlockPos.ZERO;
    private static final EmptyBlockGetter EMPTY_LEVEL = EmptyBlockGetter.INSTANCE;

    public static int[] getColor(String blockName) {
        return COLOR_CACHE.computeIfAbsent(blockName, name -> {
            var key = ResourceLocation.tryParse(name);
            if (key == null) return new int[]{0, 0, 0};

            Block block = BuiltInRegistries.BLOCK.get(key);

            try {
                MapColor mapColor = block.defaultBlockState().getMapColor(EMPTY_LEVEL, DUMMY_POS);

                if (mapColor == MapColor.NONE) return new int[]{0, 0, 0};

                int col = mapColor.col;
                int r = (col >> 16) & 0xFF;
                int g = (col >> 8) & 0xFF;
                int b = col & 0xFF;

                return new int[]{r, g, b};
            } catch (Exception e) {
                return new int[]{0, 0, 0};
            }
        });
    }

    public static int getColorInt(String blockName) {
        int[] rgb = getColor(blockName);

        return (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
    }

    public static String getColorHex(String blockName) {
        int[] rgb = getColor(blockName);

        return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }

    public static void preload() {
        BuiltInRegistries.BLOCK.keySet().forEach(key -> {
            String name = key.toString();

            if (!COLOR_CACHE.containsKey(name)) {
                getColor(name);
            }
        });
    }
}
