/*
 Event Manager
 TickInterruptedEvent.java
 Copyright © 2021  Fabius Mettner (Team Noisruker)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
     * Creates a tick interrupted event with the last ticks time and the thrown exception.
     * <p>
     * The result is set to {@code false} by default
     * @param lastTick The last ticks time
     * @param e The thrown exception
     *
     */
    public TickInterruptedEvent(long lastTick, Exception e) {
        super("tick interrupted");
        this.lastTick = lastTick;
        this.exception = e;
        this.setResult(false);
    }

    /**
     * @return The last ticks time
     */
    @SuppressWarnings("unused")
    public long getLastTick() {
        return this.lastTick;
    }

    /**
     * @return The thrown exception
     */
    @SuppressWarnings("unused")
    public Exception getException() {
        return exception;
    }
}
