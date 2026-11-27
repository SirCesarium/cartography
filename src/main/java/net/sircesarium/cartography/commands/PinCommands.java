package net.sircesarium.cartography.commands;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.sircesarium.cartography.data.pin.Pin;
import net.sircesarium.cartography.data.pin.PinRegistry;
import net.sircesarium.cartography.manager.PinManager;

public class PinCommands {

    public static LiteralCommandNode<CommandSourceStack> register() {
        return Commands.literal("pin")
                .then(Commands.literal("add")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("type", ResourceLocationArgument.id())
                                        .suggests(PinCommands::suggestPinTypes)
                                        .executes(PinCommands::addPin))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("dimension", DimensionArgument.dimension())
                                        .executes(PinCommands::removePinAt))))
                .then(Commands.literal("get")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("dimension", DimensionArgument.dimension())
                                        .executes(PinCommands::getPin))))
                .then(Commands.literal("list")
                        .executes(PinCommands::listPins))
                .then(Commands.literal("clear")
                        .executes(PinCommands::clearAll))
                .build();
    }

    private static int addPin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
        ResourceLocation typeId = ResourceLocationArgument.getId(ctx, "type");
        ServerLevel level = ctx.getSource().getLevel();
        ResourceKey<Level> dimension = level.dimension();
        ServerPlayer player = ctx.getSource().getPlayerOrException();

        var registry = ctx.getSource().getServer().registryAccess().registryOrThrow(PinRegistry.KEY);

        if (!registry.containsKey(typeId)) {
            ctx.getSource().sendFailure(Component.literal("Unknown pin type: " + typeId));
            return 0;
        }

        Pin pin = Pin.create(pos, dimension, typeId, player.getUUID());

        PinManager.addPin(pin);

        ctx.getSource().sendSuccess(() -> Component.literal("Pin added at " + pos.toShortString()), true);
        return 1;
    }

    private static int removePinAt(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
        ResourceKey<Level> dimension = DimensionArgument.getDimension(ctx, "dimension").dimension();

        if (PinManager.removePin(pos, dimension)) {
            ctx.getSource().sendSuccess(() -> Component.literal("Pin removed at " + pos.toShortString()), true);
            return 1;
        }

        ctx.getSource().sendFailure(Component.literal("No pin found at " + pos.toShortString()));
        return 0;
    }

    private static int getPin(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
        ResourceKey<Level> dimension = DimensionArgument.getDimension(ctx, "dimension").dimension();
        Pin pin = PinManager.getPin(pos, dimension);

        if (pin == null) {
            ctx.getSource().sendFailure(Component.literal("No pin found at " + pos.toShortString()));
            return 0;
        }

        MutableComponent text = Component.empty()
                .append(Component.literal("Pin ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(pin.id().toString()).withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" at ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(pin.pos().toShortString()).withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" in ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(pin.dimension().location().toString()).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("\nType: ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(pin.typeId().toString()).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("\nAuthor: ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(pin.author().toString()).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("\nTTL: ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(pin.getEffectiveTtl() + "s").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("\nExpired: ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(String.valueOf(pin.isExpired())).withStyle(ChatFormatting.AQUA));

        ctx.getSource().sendSuccess(() -> text, false);
        return 1;
    }

    private static int listPins(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        List<Pin> pins = PinManager.getPins(level.dimension());

        if (pins.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal("No pins in this dimension"), false);
            return 0;
        }

        MutableComponent text = Component.literal("Pins in " + level.dimension().location() + ":\n").withStyle(ChatFormatting.GOLD);

        for (int i = 0; i < pins.size(); i++) {
            Pin pin = pins.get(i);
            String separator = (i < pins.size() - 1) ? "\n" : "";

            text.append(Component.literal("  - ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(pin.pos().toShortString()).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" [").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(pin.typeId().toString()).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal("]").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(separator));
        }

        ctx.getSource().sendSuccess(() -> text, false);
        return pins.size();
    }

    private static int clearAll(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        List<Pin> pins = PinManager.getPins(level.dimension());
        int count = pins.size();

        for (Pin pin : pins) {
            PinManager.removePin(pin.id());
        }

        final int removed = count;
        ctx.getSource().sendSuccess(() -> Component.literal("Cleared " + removed + " pins from this dimension"), true);
        return removed;
    }

    private static CompletableFuture<Suggestions> suggestPinTypes(
            CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        var registry = ctx.getSource().getServer().registryAccess().registryOrThrow(PinRegistry.KEY);

        registry.keySet().forEach(key -> builder.suggest(key.toString()));

        return builder.buildFuture();
    }
}
