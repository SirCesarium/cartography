package net.sircesarium.cartography.data.pin;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public class PinRegistry {
    public static final ResourceKey<Registry<PinType>> KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("cartography", "pin_types"));

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener((DataPackRegistryEvent.NewRegistry event) ->
                event.dataPackRegistry(KEY, PinType.CODEC));
    }
}
