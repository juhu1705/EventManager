package de.noisruker.threading.events;

import de.noisruker.event.events.Event;

public class TickerStoppedEvent extends Event<Void> {

    public TickerStoppedEvent() {
        super("ticker stopped event");
    }
}
