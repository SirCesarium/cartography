package net.sircesarium.cartography.commands;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.sircesarium.cartography.Cartography;

@EventBusSubscriber(modid = Cartography.MODID)
public class CartographyCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("cartography")
                        .then(PinCommands.register())
                        .then(WaypointCommands.register())
        );
    }
}
