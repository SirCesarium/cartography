package net.sircesarium.cartography.api.event;

import net.neoforged.bus.api.Event;
import net.sircesarium.cartography.data.Waypoint;

/**
 * Events posted on {@code NeoForge.EVENT_BUS} when waypoints change.
 */
public abstract class CartographyWaypointEvent extends Event {
    private final Waypoint waypoint;

    protected CartographyWaypointEvent(Waypoint waypoint) {
        this.waypoint = waypoint;
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public static final class Added extends CartographyWaypointEvent {
        public Added(Waypoint waypoint) {
            super(waypoint);
        }
    }

    public static final class Updated extends CartographyWaypointEvent {
        public Updated(Waypoint waypoint) {
            super(waypoint);
        }
    }

    public static final class Removed extends CartographyWaypointEvent {
        public Removed(Waypoint waypoint) {
            super(waypoint);
        }
    }
}
