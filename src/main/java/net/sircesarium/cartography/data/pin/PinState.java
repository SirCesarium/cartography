package net.sircesarium.cartography.data.pin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PinState(
        String condition,
        String color,
        String icon,
        String mapIcon,
        String name
) {
    public static final Codec<PinState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("condition", null).forGetter(PinState::condition),
            Codec.STRING.fieldOf("color").forGetter(PinState::color),
            Codec.STRING.fieldOf("icon").forGetter(PinState::icon),
            Codec.STRING.optionalFieldOf("mapIcon", null).forGetter(PinState::mapIcon),
            Codec.STRING.optionalFieldOf("name", null).forGetter(PinState::name)
    ).apply(instance, PinState::new));

    public PinState(String color, String icon) {
        this(null, color, icon, null, null);
    }
}
