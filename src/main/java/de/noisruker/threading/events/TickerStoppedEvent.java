/*
 Event Manager
 TickerStoppedEvent.java
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
 * Is triggered if the ticker stops working.
 */
public class TickerStoppedEvent extends Event<Void> {

    /**
     * Creates a new Ticker Stopped Event
     */
    public TickerStoppedEvent() {
        super("ticker stopped event");
    }
}
