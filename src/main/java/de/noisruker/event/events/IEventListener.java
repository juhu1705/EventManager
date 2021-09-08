package de.noisruker.event.events;

public interface IEventListener<T extends Event> {

    public void listen(T event);

}
