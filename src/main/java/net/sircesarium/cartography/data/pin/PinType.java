package net.sircesarium.cartography.data.pin;

import java.util.List;

public record PinType(
        String type,
        int lifetime,
        List<String> data
) {
    public static final int DEFAULT_LIFETIME = 300;
    public static final String DUMMY = "dummy";
    public static final String BLOCK = "block";
    public static final String MOB = "mob";
    public static final String ITEM = "item";

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

    public static PinType dummy() {
        return new PinType(DUMMY, DEFAULT_LIFETIME, List.of());
    }

    public static PinType dummy(int lifetime) {
        return new PinType(DUMMY, lifetime, List.of());
    }
}
