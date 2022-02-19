package de.noisruker.threading.events;

import de.noisruker.event.events.Event;

/**
 * Is triggered when an exception occurs while ticking. Returns true if this thread should continue ticking, false otherwise.
 */
public class TickInterruptedEvent extends Event<Boolean> {

    /**
     * The last ticks time
     */
    private final long lastTick;
    /**
     * The thrown exception
     */
    private final Exception exception;

    /**
     * Creates a tick interrupted event with the last ticks time and the thrown exception
     * @param lastTick The last ticks time
     * @param e The thrown exception
     * @implNote The result is set to {@code false} by default
     */
    public TickInterruptedEvent(long lastTick, Exception e) {
        super("tick interrupted");
        this.lastTick = lastTick;
        this.exception = e;
        this.setResult(false);
    }

    /**
     * The last ticks time
     */
    @SuppressWarnings("unused")
    public long getLastTick() {
        return this.lastTick;
    }

    /**
     * The thrown exception
     */
    @SuppressWarnings("unused")
    public Exception getException() {
        return exception;
    }
}
