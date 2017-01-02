package net.damroo.androidprototype.events;

/**
 * Created by damroo on 4/30/2016.
 */
public class DisplayOrderEvent {
    public DisplayEventType type;

    public DisplayOrderEvent(DisplayEventType type) {
        this.type = type;
    }
}
