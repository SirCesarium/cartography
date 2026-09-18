package net.sircesarium.cartography.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class Waypoint {
    private final UUID id;
    private final BlockPos pos;
    private final ResourceKey<Level> dimension;
    private final String name;
    private final Integer color;
    private final ResourceLocation icon;
    private final UUID author;
    private final WaypointVisibility visibility;
    private final Set<UUID> visibleTo;
    @Setter
    private Long expiresAt;

    public Waypoint(UUID id, BlockPos pos, ResourceKey<Level> dimension, String name, Integer color, ResourceLocation icon, UUID author, Long expiresAt) {
        this(id, pos, dimension, name, color, icon, author, WaypointVisibility.PRIVATE, Set.of(), expiresAt);
    }

    public Waypoint(UUID id, BlockPos pos, ResourceKey<Level> dimension, String name, Integer color, ResourceLocation icon, UUID author, WaypointVisibility visibility, Set<UUID> visibleTo, Long expiresAt) {
        if (expiresAt != null && expiresAt != -1 && expiresAt <= 0) {
            throw new IllegalArgumentException("expiresAt must be null, -1, or > 0, got " + expiresAt);
        }

        this.id = id;
        this.pos = pos;
        this.dimension = dimension;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.author = author;
        this.visibility = visibility;
        this.visibleTo = Set.copyOf(visibleTo);
        this.expiresAt = expiresAt;
    }

    public Waypoint(BlockPos pos, ResourceKey<Level> dimension, String name, Integer color, ResourceLocation icon, UUID author, Long expiresAt) {
        this(UUID.randomUUID(), pos, dimension, name, color, icon, author, expiresAt);
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt != -1 && System.currentTimeMillis() > expiresAt;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        
        tag.putString("id", id.toString());
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        tag.putString("dimension", dimension.location().toString());
        tag.putString("name", name);

        if (color != null) {
            tag.putInt("color", color);
        }

        tag.putString("icon", icon.toString());
        tag.putString("author", author.toString());
        tag.putString("visibility", visibility.name().toLowerCase());

        if (!visibleTo.isEmpty()) {
            ListTag list = new ListTag();
            for (UUID uuid : visibleTo) {
                list.add(StringTag.valueOf(uuid.toString()));
            }
            tag.put("visibleTo", list);
        }

        if (expiresAt != null) {
            tag.putLong("expiresAt", expiresAt);
        }

        return tag;
    }

    public static Waypoint fromNBT(CompoundTag tag) {
        WaypointVisibility visibility = WaypointVisibility.PRIVATE;
        Set<UUID> visibleTo = Set.of();

        if (tag.contains("visibility")) {
            visibility = WaypointVisibility.valueOf(tag.getString("visibility").toUpperCase());
        }

        if (tag.contains("visibleTo")) {
            ListTag list = tag.getList("visibleTo", Tag.TAG_STRING);
            HashSet<UUID> set = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                set.add(UUID.fromString(list.getString(i)));
            }
            visibleTo = Set.copyOf(set);
        }

        return new Waypoint(
                tag.contains("id") ? UUID.fromString(tag.getString("id")) : UUID.randomUUID(),
                new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")),
                tag.contains("dimension")
                        ? ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("dimension")))
                        : Level.OVERWORLD,
                tag.getString("name"),
                tag.contains("color") ? tag.getInt("color") : null,
                ResourceLocation.parse(tag.getString("icon")),
                UUID.fromString(tag.getString("author")),
                visibility,
                visibleTo,
                tag.contains("expiresAt") ? tag.getLong("expiresAt") : null
        );
    }
}
