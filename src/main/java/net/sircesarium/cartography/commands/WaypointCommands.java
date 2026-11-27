package net.sircesarium.cartography.commands;

import java.util.List;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.sircesarium.cartography.data.Waypoint;
import net.sircesarium.cartography.data.storage.WaypointData;
import net.sircesarium.cartography.manager.WaypointManager;

public class WaypointCommands {

    public static LiteralCommandNode<CommandSourceStack> register() {
        return Commands.literal("waypoint")
                .then(Commands.literal("add")
                        .then(Commands.argument("name", StringArgumentType.string())
                                // pos optional: with explicit coords or at player's feet
                                .executes(WaypointCommands::addWaypointAtSelf)
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(WaypointCommands::addWaypoint))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(WaypointCommands::removeWaypoint)))
                .then(Commands.literal("list")
                        .executes(WaypointCommands::listWaypoints))
                .then(Commands.literal("clear")
                        .executes(WaypointCommands::clearAll))
                .build();
    }

    private static int addWaypointAtSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        return addWaypointAt(ctx, player.blockPosition());
    }

    private static int addWaypoint(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
        return addWaypointAt(ctx, pos);
    }

    private static int addWaypointAt(CommandContext<CommandSourceStack> ctx, BlockPos pos) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "name");
        ServerLevel level = ctx.getSource().getLevel();
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ResourceKey<Level> dimension = level.dimension();

        WaypointData data = WaypointManager.getOrCreate(level);

        if (data.hasWaypointAt(pos, dimension)) {
            ctx.getSource().sendFailure(Component.literal("Waypoint already exists at " + pos.toShortString()));
            return 0;
        }

        data.addWaypoint(pos, dimension, name, null, ResourceLocation.parse("minecraft:waypoint"), player.getUUID(), null);

        ctx.getSource().sendSuccess(() -> Component.literal("Waypoint '" + name + "' added at " + pos.toShortString()), true);
        return 1;
    }

    private static int removeWaypoint(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "name");
        ServerLevel level = ctx.getSource().getLevel();
        WaypointData data = WaypointManager.getOrCreate(level);

        boolean removed = data.getWaypoints().stream()
                .filter(wp -> wp.getName().equals(name))
                .findFirst()
                .map(wp -> data.removeWaypoint(wp.getId()))
                .orElse(false);

        if (removed) {
            ctx.getSource().sendSuccess(() -> Component.literal("Waypoint '" + name + "' removed"), true);
            return 1;
        }

        ctx.getSource().sendFailure(Component.literal("Waypoint '" + name + "' not found"));
        return 0;
    }

    private static int listWaypoints(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        List<Waypoint> waypoints = WaypointManager.getOrCreate(level).getWaypoints();

        if (waypoints.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal("No waypoints in this dimension"), false);
            return 0;
        }

        MutableComponent text = Component.literal("Waypoints in " + level.dimension().location() + ":\n").withStyle(ChatFormatting.GOLD);

        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint wp = waypoints.get(i);
            String separator = (i < waypoints.size() - 1) ? "\n" : "";

            text.append(Component.literal("  - ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(wp.getName()).withStyle(ChatFormatting.AQUA))
                    .append(Component.literal(" at ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(wp.getPos().toShortString()).withStyle(ChatFormatting.YELLOW))
                    .append(Component.literal(" by ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(wp.getVisibility().name()).withStyle(ChatFormatting.GREEN))
                    .append(Component.literal(separator));
        }

        ctx.getSource().sendSuccess(() -> text, false);
        return waypoints.size();
    }

    private static int clearAll(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        WaypointData data = WaypointManager.getOrCreate(level);
        List<Waypoint> waypoints = data.getWaypoints();
        int count = waypoints.size();

        for (Waypoint wp : waypoints) {
            data.removeWaypoint(wp.getId());
        }

        final int removed = count;
        ctx.getSource().sendSuccess(() -> Component.literal("Cleared " + removed + " waypoints from this dimension"), true);
        return removed;
    }
}
