package net.sircesarium.cartography.data.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.sircesarium.cartography.data.pin.Pin;
import net.sircesarium.cartography.util.NBTData;

/**
 * Persists active pins to {@code cartography/pins.dat} inside the world save.
 */
public class PinData extends NBTData {
    private List<Pin> pins = new ArrayList<>();

    public PinData(ServerLevel level) {
        super(level, "pins.dat");
    }

    public List<Pin> getPins() {
        return pins;
    }

    public void setPins(List<Pin> pins) {
        this.pins = new ArrayList<>(pins);
        markDirty();
    }

    @Override
    protected CompoundTag toNBT() {
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();

        for (Pin pin : pins) {
            if (!pin.isExpired()) {
                list.add(pinToNBT(pin));
            }
        }

        root.put("pins", list);

        return root;
    }

    @Override
    protected void fromNBT(CompoundTag tag) {
        pins.clear();

        ListTag list = tag.getList("pins", Tag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            Pin pin = pinFromNBT(list.getCompound(i));

            if (!pin.isExpired()) {
                pins.add(pin);
            }
        }
    }

    private static CompoundTag pinToNBT(Pin pin) {
        CompoundTag tag = new CompoundTag();

        tag.putString("id", pin.id().toString());
        tag.putInt("x", pin.pos().getX());
        tag.putInt("y", pin.pos().getY());
        tag.putInt("z", pin.pos().getZ());
        tag.putString("dimension", pin.dimension().location().toString());
        tag.putString("typeId", pin.typeId().toString());
        tag.putString("author", pin.author().toString());
        tag.putLong("createdAt", pin.createdAt());

        if (pin.ttl() != null) {
            tag.putLong("ttl", pin.ttl());
        }

        if (!pin.pinData().isEmpty()) {
            tag.put("pinData", mapToNBT(pin.pinData()));
        }

        return tag;
    }

    private static Pin pinFromNBT(CompoundTag tag) {
        UUID id = UUID.fromString(tag.getString("id"));
        BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        ResourceKey<Level> dimension = ResourceKey.create(
                Registries.DIMENSION,
                ResourceLocation.parse(tag.getString("dimension"))
        );
        ResourceLocation typeId = ResourceLocation.parse(tag.getString("typeId"));
        UUID author = UUID.fromString(tag.getString("author"));
        long createdAt = tag.getLong("createdAt");
        Long ttl = tag.contains("ttl") ? tag.getLong("ttl") : null;
        Map<String, Object> data = tag.contains("pinData") ? nbtToMap(tag.getCompound("pinData")) : Map.of();

        return new Pin(id, pos, dimension, typeId, author, createdAt, ttl, data);
    }

    private static CompoundTag mapToNBT(Map<String, Object> data) {
        CompoundTag tag = new CompoundTag();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Boolean b) {
                tag.putBoolean(key, b);
            } else if (value instanceof Integer i) {
                tag.putInt(key, i);
            } else if (value instanceof Long l) {
                tag.putLong(key, l);
            } else if (value instanceof Float f) {
                tag.putFloat(key, f);
            } else if (value instanceof Double d) {
                tag.putDouble(key, d);
            } else if (value != null) {
                tag.putString(key, String.valueOf(value));
            }
        }

        return tag;
    }

    private static Map<String, Object> nbtToMap(CompoundTag tag) {
        Map<String, Object> map = new HashMap<>();

        for (String key : tag.getAllKeys()) {
            Tag value = tag.get(key);

            if (value == null) continue;

            switch (value.getId()) {
                case Tag.TAG_BYTE -> map.put(key, tag.getBoolean(key));
                case Tag.TAG_INT -> map.put(key, tag.getInt(key));
                case Tag.TAG_LONG -> map.put(key, tag.getLong(key));
                case Tag.TAG_FLOAT -> map.put(key, tag.getFloat(key));
                case Tag.TAG_DOUBLE -> map.put(key, tag.getDouble(key));
                default -> map.put(key, tag.getString(key));
            }
        }

        return Map.copyOf(map);
    }
}
