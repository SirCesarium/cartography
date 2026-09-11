package net.sircesarium.cartography.client.events;

import java.util.List;
import java.util.ListIterator;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.sircesarium.cartography.config.CartographyServerConfig;

@EventBusSubscriber(modid = "cartography", value = Dist.CLIENT)
public class DebugOverlayHandler {

    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        List<String> left = event.getLeft();
        if (CartographyServerConfig.isRestricted(CartographyServerConfig.hideF3Coordinates))
            left.removeIf(l -> l.startsWith("XYZ:"));
        if (CartographyServerConfig.isRestricted(CartographyServerConfig.hideF3Block))
            left.removeIf(l -> l.startsWith("Block:"));
        if (CartographyServerConfig.isRestricted(CartographyServerConfig.hideF3Chunk))
            left.removeIf(l -> l.startsWith("Chunk:"));
        if (CartographyServerConfig.isRestricted(CartographyServerConfig.hideF3Facing))
            left.removeIf(l -> l.startsWith("Facing:"));
        if (CartographyServerConfig.isRestricted(CartographyServerConfig.hideF3Biome))
            left.removeIf(l -> l.startsWith("Biome:"));
        if (CartographyServerConfig.isRestricted(CartographyServerConfig.hideF3Light))
            left.removeIf(l -> l.startsWith("Client Light:"));

        if (CartographyServerConfig.isRestricted(CartographyServerConfig.hideF3Targeted))
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
