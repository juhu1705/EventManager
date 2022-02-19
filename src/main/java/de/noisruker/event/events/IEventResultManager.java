package de.noisruker.event.events;

/**
 * Handles the result of an event. This class is used for async event handling.
 * @param <T> The events result type to handle
 */
public interface IEventResultManager<T> {

    /**
     * Handles the result of an event
     * @param result The result to handle. {@code null} if no result is set.
     */
    void handle(T result);

}
