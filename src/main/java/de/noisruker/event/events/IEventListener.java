package de.noisruker.event.events;

/**
 * This is an event listener, waiting to the event {@link T} to be triggered.
 *
 * @param <T> The class this listener is listening for
 * @implNote To register your IEventListener use {@link de.noisruker.event.EventManager#registerEventListener(Class, IEventListener)}
 * @author Fabius Mettner
 */
public interface IEventListener<T extends Event> {

    /**
     * This is the called method when the listened event was triggert
     *
     * @param event The triggert event
     */
    public void listen(T event);

}
