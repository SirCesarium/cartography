package net.sircesarium.cartography.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

@Getter
public class Waypoint {
    private final BlockPos pos;
    private final ResourceKey<Level> dimension;
    private final String name;
    private final Integer color;
    private final ResourceLocation icon;
    private final UUID author;
    @Setter
    private Long expiresAt;

    public Waypoint(BlockPos pos, ResourceKey<Level> dimension, String name, Integer color, ResourceLocation icon, UUID author, Long expiresAt) {
        this.pos = pos;
        this.dimension = dimension;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.author = author;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != null && System.currentTimeMillis() > expiresAt;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
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

        if (expiresAt != null) {
            tag.putLong("expiresAt", expiresAt);
        }

        return tag;
    }

    public static Waypoint fromNBT(CompoundTag tag) {
        return new Waypoint(
                new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")),
                tag.contains("dimension")
                    ? ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("dimension")))
                    : Level.OVERWORLD,
                tag.getString("name"),
                tag.contains("color") ? tag.getInt("color") : null,
                ResourceLocation.parse(tag.getString("icon")),
                UUID.fromString(tag.getString("author")),
                tag.contains("expiresAt") ? tag.getLong("expiresAt") : null
        );
    }
}
