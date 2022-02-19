package de.noisruker.threading.events;

import de.noisruker.event.events.Event;

/**
 * Is triggered if the ticker stops working.
 */
public class TickerStoppedEvent extends Event<Void> {

    public TickerStoppedEvent() {
        super("ticker stopped event");
    }
}
