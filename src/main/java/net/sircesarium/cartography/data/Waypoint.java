package net.sircesarium.cartography.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

@Getter
public class Waypoint {
    private final int x;
    private final int y;
    private final int z;
    private final String name;
    private final int color;
    private final ResourceLocation icon;
    private final UUID author;
    @Setter
    private Long expiresAt;

    public Waypoint(int x, int y, int z, String name, int color, ResourceLocation icon, UUID author, Long expiresAt) {
        this.x = x;
        this.y = y;
        this.z = z;
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
        tag.putInt("x", x);
        tag.putInt("y", y);
        tag.putInt("z", z);
        tag.putString("name", name);
        tag.putInt("color", color);
        tag.putString("icon", icon.toString());
        tag.putString("author", author.toString());

        if (expiresAt != null) {
            tag.putLong("expiresAt", expiresAt);
        }

        return tag;
    }

    public static Waypoint fromNBT(CompoundTag tag) {
        return new Waypoint(
                tag.getInt("x"),
                tag.getInt("y"),
                tag.getInt("z"),
                tag.getString("name"),
                tag.getInt("color"),
                ResourceLocation.parse(tag.getString("icon")),
                UUID.fromString(tag.getString("author")),
                tag.contains("expiresAt") ? tag.getLong("expiresAt") : null
        );
    }
}
