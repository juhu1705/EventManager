/*
 Event Manager
 IEventListener.java
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

package de.noisruker.event.events;

/**
 * This is an event listener, waiting to the event {@link T} to be triggered.
 *
 * @param <T> The class this listener is listening for
 * @implNote To register your IEventListener use {@link de.noisruker.event.EventManager#registerEventListener(Class, IEventListener)}
 */
public interface IEventListener<T extends Event> {

    /**
     * This is the called method when the listened event was triggert
     *
     * @param event The triggert event
     */
    public void listen(T event);

}
