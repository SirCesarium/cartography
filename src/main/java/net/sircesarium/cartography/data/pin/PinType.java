package net.sircesarium.cartography.data.pin;

import java.util.Map;

public record PinType(
        String type,
        String icon,
        int defaultTtl,
        Map<String, String> pinData
) {
    public static final int DEFAULT_TTL = 300;
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

    public static PinType dummy(String icon) {
        return new PinType(DUMMY, icon, DEFAULT_TTL, Map.of());
    }

    public static PinType dummy(String icon, int defaultTtl) {
        return new PinType(DUMMY, icon, defaultTtl, Map.of());
    }
}
