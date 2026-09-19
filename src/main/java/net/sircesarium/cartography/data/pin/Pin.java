package net.sircesarium.cartography.data.pin;

import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record Pin(
        UUID id,
        BlockPos pos,
        ResourceKey<Level> dimension,
        ResourceLocation typeId,
        UUID author,
        long createdAt,
        Long ttl,
        Map<String, Object> pinData
) {
    public boolean isExpired() {
        long effectiveTtl = (ttl != null) ? ttl : 300;
        return System.currentTimeMillis() > createdAt + (effectiveTtl * 1000);
    }

    public long getEffectiveTtl() {
        return ttl != null ? ttl : 300;
    }

    public static Pin create(BlockPos pos, ResourceKey<Level> dimension, ResourceLocation typeId, UUID author) {
        return new Pin(UUID.randomUUID(), pos, dimension, typeId, author, System.currentTimeMillis(), null, Map.of());
    }

    public static Pin create(BlockPos pos, ResourceKey<Level> dimension, ResourceLocation typeId, UUID author, long ttl, Map<String, Object> pinData) {
        return new Pin(UUID.randomUUID(), pos, dimension, typeId, author, System.currentTimeMillis(), ttl, pinData);
    }
}
