package de.noisruker.threading.events;

import de.noisruker.event.events.Event;

public class TickEvent extends Event<Boolean> {

    private final long tick;

    public TickEvent(long tick) {
        super("tick event");
        this.tick = tick;
        this.setResult(true);
    }

    public long getCurrentTick() {
        return this.tick;
    }

}
