package net.sircesarium.cartography.data.pin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record PinState(
        Optional<String> condition,
        String color,
        String icon,
        Optional<String> mapIcon,
        Optional<String> name
) {
    public static final Codec<PinState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("condition").forGetter(PinState::condition),
            Codec.STRING.fieldOf("color").forGetter(PinState::color),
            Codec.STRING.fieldOf("icon").forGetter(PinState::icon),
            Codec.STRING.optionalFieldOf("mapIcon").forGetter(PinState::mapIcon),
            Codec.STRING.optionalFieldOf("name").forGetter(PinState::name)
    ).apply(instance, PinState::new));

    public PinState(String color, String icon) {
        this(Optional.empty(), color, icon, Optional.empty(), Optional.empty());
    }
}
