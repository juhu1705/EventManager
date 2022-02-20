package de.noiruker.event;

import de.noisruker.event.EventManager;
import de.noisruker.event.events.Event;

public class SomeEvent<T extends SomeEvent.SomeClass> extends Event<Integer> {

    T t;

    /**
     * Creates a new Event with the specified name. To trigger the event use {@link EventManager#triggerEvent(Event)} with this event as parameter.
     * If you expect an output the triggerEvent Method will return it for you after handling all listeners for this event.
     *
     * @param name The events name
     * @param t
     */
    public SomeEvent(String name, T t) {
        super(name);
        this.t = t;
    }

    public static class SomeClass {

    }

    public static class SomeChildEvent extends SomeEvent<SomeClass> {

        /**
         * Creates a new Event with the specified name. To trigger the event use {@link EventManager#triggerEvent(Event)} with this event as parameter.
         * If you expect an output the triggerEvent Method will return it for you after handling all listeners for this event.
         */
        public SomeChildEvent(SomeClass t) {
            super("nichts", t);
        }
    }

}
