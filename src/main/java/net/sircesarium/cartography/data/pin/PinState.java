package net.sircesarium.cartography.data.pin;

public record PinState(
        String condition,
        String color,
        String icon,
        String mapIcon,
        String name
) {
    public PinState(String color, String icon) {
        this(null, color, icon, null, null);
    }
}
