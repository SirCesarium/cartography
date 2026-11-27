package net.sircesarium.cartography.data.pin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

import net.sircesarium.cartography.data.pin.context.PinContextRegistry;

public record PinType(
        String type,
        int lifetime,
        List<String> data
) {
    public static final int DEFAULT_LIFETIME = 300;

    public static final Codec<PinType> CODEC = RecordCodecBuilder.<PinType>create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(PinType::type),
            Codec.INT.optionalFieldOf("lifetime", DEFAULT_LIFETIME).forGetter(PinType::lifetime),
            Codec.STRING.listOf().optionalFieldOf("data", List.of()).forGetter(PinType::data)
    ).apply(instance, PinType::new)).flatXmap(PinType::validate, PinType::validate);

    public static final String DUMMY = "dummy";
    public static final String BLOCK = "block";
    public static final String MOB = "mob";
    public static final String ITEM = "item";
    public static final String PLAYER = "player";

    public boolean isDummy() {
        return DUMMY.equals(type);
    }

    public boolean isBlock() {
        return BLOCK.equals(type);
    }

    public boolean isMob() {
        return MOB.equals(type);
    }

    public boolean isItem() {
        return ITEM.equals(type);
    }

    public boolean isPlayer() {
        return PLAYER.equals(type);
    }

    public static PinType dummy() {
        return new PinType(DUMMY, DEFAULT_LIFETIME, List.of());
    }

    public static PinType dummy(int lifetime) {
        return new PinType(DUMMY, lifetime, List.of());
    }

    private static DataResult<PinType> validate(PinType pt) {
        if (!pt.isDummy() && !PinContextRegistry.hasContext(pt.type())) {
            return DataResult.error(() -> "Unknown pin type: '" + pt.type() + "'. Must be 'dummy' or a registered PinDataContext");
        }

        if (pt.isDummy() && !pt.data().isEmpty()) {
            return DataResult.error(() -> "Dummy pin type cannot have data");
        }

        for (String getter : pt.data()) {
            try {
                PinContextRegistry.getGetter(pt.type(), getter);
            } catch (IllegalArgumentException e) {
                return DataResult.error(e::getMessage);
            }
        }

        return DataResult.success(pt);
    }
}
