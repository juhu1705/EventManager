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
 * Stellt einen Event zuhörer dar, der auf das Event {@link T} wartet.
 *
 * @param <T> Die Event-klasse auf die der Zuhörer wartet
 */
public interface IEventListener<T extends Event> {

    /**
     * Diese Methode wird nach Registrierung immer dann ausgeführt, wenn das angegebene Event ausgelöst wird.
     *
     * @param event Das Event, das ausgelöst wurde
     */
    public void listen(T event);

}
