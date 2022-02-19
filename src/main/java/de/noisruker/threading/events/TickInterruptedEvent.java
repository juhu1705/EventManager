package de.noisruker.threading.events;

import de.noisruker.event.events.Event;

public class TickInterruptedEvent extends Event<Boolean> {

    private final long lastTick;

    public TickInterruptedEvent(long lastTick) {
        super("tick interrupted");
        this.lastTick = lastTick;
    }

    public long getLastTick() {
        return this.lastTick;
    }
}
