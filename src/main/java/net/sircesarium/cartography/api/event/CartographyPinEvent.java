package net.sircesarium.cartography.api.event;

import net.neoforged.bus.api.Event;
import net.sircesarium.cartography.data.pin.Pin;

/**
 * Events posted on {@code NeoForge.EVENT_BUS} when pins change.
 */
public abstract class CartographyPinEvent extends Event {
    private final Pin pin;

    protected CartographyPinEvent(Pin pin) {
        this.pin = pin;
    }

    public Pin getPin() {
        return pin;
    }

    public static final class Added extends CartographyPinEvent {
        public Added(Pin pin) {
            super(pin);
        }
    }

    public static final class Removed extends CartographyPinEvent {
        public Removed(Pin pin) {
            super(pin);
        }
    }

    public static final class Expired extends CartographyPinEvent {
        public Expired(Pin pin) {
            super(pin);
        }
    }
}
