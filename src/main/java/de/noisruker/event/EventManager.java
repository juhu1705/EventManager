/*
 Event Manager
 EventManager.java
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

package de.noisruker.event;

import de.noisruker.event.events.Event;
import de.noisruker.event.events.EventListener;
import de.noisruker.event.events.IEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class EventManager {

    private static final EventManager instance = new EventManager();

    public static EventManager getInstance() {
        return instance;
    }

    private final ArrayList<EventListenerHolder<? extends Event>> listeners;

    protected EventManager() {
        this.listeners = new ArrayList<>();
    }

    public void registerEventListeners(Class<?> c) {
        if(c == null)
            return;
        for(Method m: c.getDeclaredMethods()) {
            if(m.isAnnotationPresent(EventListener.class)) {
                try {
                    if(!m.canAccess(null)) {
                        System.err.println("Method " + m.getName() + " can't be executed by the EventManager. Please ensure the Method to be public and static!");
                        continue;
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Method " + m.getName() + " can't be executed by the EventManager. Please ensure the Method to be public and static!");
                    continue;
                }

                Parameter[] params = m.getParameters();
                if(params.length == 1 && Event.class.isAssignableFrom(params[0].getType())) {
                    Class<? extends Event> eventClass = (Class<? extends Event>) params[0].getType();

                    this.put(eventClass, event -> {
                        try {
                            m.invoke(null, eventClass.cast(event));
                        } catch (InvocationTargetException e) {
                            System.err.println("Event was invoked from target.");
                        } catch (IllegalAccessException | IllegalArgumentException e) {
                            System.err.println("Could not fire Event due to missing permissions.");
                        }
                    });
                }
            }
        }
    }

    public <eventClass extends Event> void registerEventListener(Class<? extends eventClass> eventName, IEventListener<eventClass> listener) {
        this.put(eventName, listener);
    }

    public <eventClass extends Event> void removeEventListener(Class<? extends eventClass> eventClassO, IEventListener<eventClass> listener) {
        if(this.containsKey(eventClassO))
            this.get(eventClassO).remove(listener);
    }

    public <eventClass extends Event> Object fireEvent(final eventClass event) {
        if(event == null)
            return null;
        if(this.containsKey(event.getClass())) {
            for(IEventListener<eventClass> listener: this.<eventClass>get((Class<? extends eventClass>) event.getClass()).getListeners()) {
                listener.listen(event);
            }
        }

        return event.getResult();
    }

    private <eventClass extends Event> boolean containsKey(Class<? extends eventClass> key) {
        for (EventListenerHolder<? extends Event> holder: this.listeners) {
            if(holder.eventsClass.equals(key))
                return true;
        }
        return false;
    }

    private <eventClass extends Event> EventListenerHolder<eventClass> get(Class<? extends eventClass> eventClass) {
        for (EventListenerHolder<? extends Event> holder: this.listeners) {
            if(holder.eventsClass.equals(eventClass))
                return (EventListenerHolder<eventClass>) holder;
        }
        EventListenerHolder<eventClass> newHolder = new EventListenerHolder<>(eventClass);
        this.listeners.add(newHolder);
        return newHolder;
    }

    private <eventClass extends Event> void put(Class<? extends eventClass> eventClass, IEventListener<eventClass> listener) {
        EventListenerHolder<eventClass> holder = this.get(eventClass);
        holder.addListener(listener);
    }

    private static class EventListenerHolder<T extends Event> {
        private final Class<? extends T> eventsClass;
        private final ArrayList<IEventListener<T>> listeners;

        EventListenerHolder(Class<? extends T> eventsClass) {
            this.eventsClass = eventsClass;
            this.listeners = new ArrayList<>();
        }

        public void addListener(IEventListener<T> eventListener) {
            this.listeners.add(eventListener);
        }

        public ArrayList<IEventListener<T>> getListeners() {
            return this.listeners;
        }

        public <eventClass extends Event> void remove(IEventListener<? extends eventClass> listener) {
            this.listeners.remove(listener);
        }
    }

}
