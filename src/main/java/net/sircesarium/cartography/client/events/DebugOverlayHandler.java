package net.sircesarium.cartography.client.events;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.config.CartographyServerConfig;
import net.sircesarium.cartography.config.F3Restriction;
import net.sircesarium.cartography.helpers.F3RestrictionHelper;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Cartography.MODID, value = Dist.CLIENT)
public class DebugOverlayHandler {

    private static final Map<ModConfigSpec.EnumValue<F3Restriction>, String> HIDE_PREFIXES = Map.of(
            CartographyServerConfig.hideF3Coordinates, "XYZ:",
            CartographyServerConfig.hideF3Block, "Block:",
            CartographyServerConfig.hideF3Chunk, "Chunk:",
            CartographyServerConfig.hideF3Facing, "Facing:",
            CartographyServerConfig.hideF3Biome, "Biome:",
            CartographyServerConfig.hideF3Light, "Client Light:"
    );

    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        List<String> left = event.getLeft();

        HIDE_PREFIXES.forEach((setting, prefix) -> {
            if (F3RestrictionHelper.isRestricted(setting))
                left.removeIf(l -> l.startsWith(prefix));
        });

        if (F3RestrictionHelper.isRestricted(CartographyServerConfig.hideF3Targeted))
            removeTargetedSection(event.getRight());
    }

    private static void removeTargetedSection(List<String> lines) {
        ListIterator<String> it = lines.listIterator();
        while (it.hasNext()) {
            String line = it.next();

            if (line.contains("Targeted Block:") || line.contains("Targeted Fluid:") || line.contains("Targeted Entity")) {
                it.remove();

                while (it.hasNext()) {
                    String next = it.next();
                    it.remove();

                    if (next.isEmpty()) break;
                }
            }
        }
    }
}
