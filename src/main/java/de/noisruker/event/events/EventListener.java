/*
 Event Manager
 EventListener.java
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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This @interface declares the following method as an EventListener method, which means that this method will be fired when the first parameters' event occurs.
 * <p>
 * To register the class of this method use {@link de.noisruker.event.EventManager#registerEventListeners(Class, Object)}.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventListener {
}
