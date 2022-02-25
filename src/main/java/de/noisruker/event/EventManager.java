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
import de.noisruker.event.events.IEventResultManager;
import de.noisruker.threading.ThreadManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for handling all event managing.
 * <br>
 *     To listen to an event you can choose one of the following options:
 *     <ul>
 *         <li>Register an {@link IEventListener event listener} to a class via the {@link EventManager#registerEventListener(Class, IEventListener)} method.</li>
 *         <li>Register a class as a event ManagerClass via the {@link EventManager#registerEventListeners(Class, Object)} method.</li>
 *     </ul>
 *     Every time an event of the registered Class is fired the EventManager calls the Method.
 * <br>
 *
 * <p>
 *     To trigger an event you just call {@link EventManager#triggerEvent(Event)} or {@link EventManager#triggerEventAsync(Event, IEventResultManager)} for async event handling, with the event you want to fire. The method returns the output of the event or give it to the {@link IEventResultManager} if it is triggered async.
 */
public class EventManager {

    /**
     * The main instance of the {@link EventManager}
     */
    private static final EventManager instance = new EventManager();

    /**
     * @return The {@link EventManager#instance main instance} of the {@link EventManager}.
     */
    @SuppressWarnings("unused")
    public static EventManager getInstance() {
        return instance;
    }

    /**
     * A list of all known {@link EventListenerHolder}.
     */
    private final ArrayList<EventListenerHolder<? extends Event<?>>> listeners;

    /**
     * Creates an {@link EventManager}.
     * <p>
     * Note: This method should only be called once while initializing the {@link EventManager#instance}.
     * Please use {@link EventManager#getInstance()} to get the current active instance of this class.
     */
    protected EventManager() {
        this.listeners = new ArrayList<>();
    }

    /**
     * Registers all event listening Methods of a class. All those Methods must annotate the {@link EventListener} @interface.
     * Please consider your methods are accessible by the classObject. If they are not an error will be thrown, and you will not receive any event
     * @param <eventClass> The events class. Your class and the classObject if not null should extend this same class.
     * @param c The class containing all the event listeners
     * @param classObject An instance of the class or {@code null}, if all methods are public and static
     */
    public <eventClass>void registerEventListeners(Class<eventClass> c, final eventClass classObject) {
        // Checks if a class is given and then checks all declared Methods of this class
        if(c == null)
            return;
        for(Method m: c.getDeclaredMethods()) {
            // Checks for all methods holding the EventListener annotation
            if(m.isAnnotationPresent(EventListener.class)) {
                // Testing if the classObject has access to the method
                try {
                    if(!m.canAccess(classObject)) {
                        System.err.println("Method " + m.getName() + " can't be executed by the EventManager. Please ensure the Method to be public and static!");
                        continue;
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Method " + m.getName() + " can't be executed by the EventManager. Please ensure the Method to be public and static!");
                    continue;
                }

                // Checks if the EventParameters are valid
                Parameter[] params = m.getParameters();
                if(params.length == 1 && Event.class.isAssignableFrom(params[0].getType())) {
                    Class<? extends Event<?>> eventClass = (Class<? extends Event<?>>) params[0].getType();

                    // Register the event for this method
                    this.put(eventClass, event -> {
                        try {
                            m.invoke(classObject, eventClass.cast(event));
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

    /**
     * Register an event handler
     *
     * @param eventClassO The events class object (On wich event the Listener wants to listen)
     * @param listener The listener to call on event trigger (The listener that will be called when an event with the events class is triggered)
     * @param <eventClass> The events class
     */
    public synchronized <eventClass extends Event> void registerEventListener(Class<? extends eventClass> eventClassO, IEventListener<eventClass> listener) {
        this.put(eventClassO, listener);
    }

    /**
     * Removes a listener from the listeners list
     *
     * @param eventClassO The class to remove the listener from
     * @param listener The listener to remove
     * @param <eventClass> The Event class
     */
    @SuppressWarnings("unused")
    public synchronized <eventClass extends Event> void removeEventListener(Class<? extends eventClass> eventClassO, IEventListener<eventClass> listener) {
        if(this.containsKey(eventClassO))
            this.get(eventClassO).remove((IEventListener<? extends Event<?>>) listener);
    }

    /**
     * Triggers an event
     *
     * @param event The event to trigger
     * @param <T> Return type of the Event
     * @param <eventClass> The events class
     * @return The events result or {@code null} if the event has no result set.
     */
    public <T, eventClass extends Event<T>> T triggerEvent(final eventClass event) {
        if(event == null)
            return null;

        for(EventListenerHolder<?> l: this.listeners)
            l.callListener(event);

        return event.getResult();
    }

    /**
     * Triggers an event on an async thread in the future. After the event is handled the given resultManager is called to handle the events output.
     *
     * @param event The event to trigger
     * @param resultManager The result manager instance to handle the events return
     * @param <T> The events return type
     * @param <eventClass> The events class
     */
    @SuppressWarnings("unused")
    public <T, eventClass extends Event<T>>void triggerEventAsync(final eventClass event, final IEventResultManager<T> resultManager) {
        ThreadManager.getInstance().executeAsync(() -> resultManager.handle(this.triggerEvent(event)));
    }

    /**
     * Checks if the EventManager know an Event that is named by the key class
     *
     * @param key The key to search for
     * @param <eventClass> The class of the key
     * @return If the event class is contained in the event managers events
     */
    private <eventClass extends Event<?>> boolean containsKey(Class<? extends eventClass> key) {
        for (EventListenerHolder<? extends Event<?>> holder: this.listeners) {
            if(holder.eventsClass.equals(key))
                return true;
        }
        return false;
    }

    /**
     * Returns the event Classes {@link EventListenerHolder}.
     *
     * @param eventClass The class to get the EventListener for
     * @param <eventClass> The class
     * @return The {@link EventListenerHolder} for the given event
     */
    private <eventClass extends Event<?>> EventListenerHolder<eventClass> get(Class<? extends eventClass> eventClass) {
        for (EventListenerHolder<? extends Event<?>> holder: this.listeners) {
            if(holder.eventsClass.equals(eventClass))
                return (EventListenerHolder<eventClass>) holder;
        }
        EventListenerHolder<eventClass> newHolder = new EventListenerHolder<>((Class<eventClass>) eventClass);
        this.listeners.add(newHolder);
        return newHolder;
    }

    /**
     * Adds the given {@link IEventListener} to the eventClasses {@link EventListenerHolder} or create a new one if no {@link EventListenerHolder} exists.
     *
     * @param eventClass The events class
     * @param listener The listener for the event
     * @param <eventClass> The class the listener must handle and the eventClass must extend
     */
    private <eventClass extends Event<?>> void put(Class<? extends eventClass> eventClass, IEventListener<eventClass> listener) {
        EventListenerHolder<eventClass> holder = this.get(eventClass);
        holder.addListener(listener);
    }

    /**
     * The event listener holder is used to hold the list of {@link IEventListener}s for the Specific events class
     * @param <T> The class of the Event, for which the holder holds listeners
     */
    private static class EventListenerHolder<T extends Event<?>> {

        /**
         * The event Class that the holder holds listener for
         */
        private final Class<T> eventsClass;
        /**
         * The listeners the holder holds
         */
        private final List<IEventListener<T>> listeners;

        /**
         * Creates a new EventListenerHolder for the given class, with no listeners
         *
         * @param eventsClass The class to create the holder for
         */
        EventListenerHolder(Class<T> eventsClass) {
            this.eventsClass = eventsClass;
            this.listeners = new ArrayList<>();
        }

        /**
         * If this EventListener instance is listening to the given events object.
         *
         * @param event The event to check listening for
         * @return If the event object is an instance of the EventListenerHolders event class
         */
        public boolean isListeningTo(Event<?> event) {
            return eventsClass.isInstance(event);
        }

        /**
         * Checks whether this EventListenerHolder is listening to the given event object and calls the listeners if it is so.
         * @param event The triggered event to call the EventListeners for
         */
        public void callListener(final Event<?> event) {
            if(this.isListeningTo(event)) {
                for(IEventListener<T> l: this.listeners)
                    l.listen(eventsClass.cast(event));
            }
        }

        /**
         * Adds a Listener to this holder
         *
         * @param eventListener The listener to add
         */
        public synchronized void addListener(IEventListener<T> eventListener) {
            this.listeners.add(eventListener);
        }

        /**
         * @return A list of all Listeners listen to the {@link EventListenerHolder#eventsClass event} of the listener
         */
        @SuppressWarnings("unused")
        public List<IEventListener<T>> getListeners() {
            return this.listeners;
        }

        /**
         * Removes the given listener if existing
         *
         * @param listener The listener to remove
         */
        public synchronized void remove(IEventListener<? extends Event<?>> listener) {
            this.listeners.remove(listener);
        }
    }

}
