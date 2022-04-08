package de.noiruker.event;

import de.noisruker.event.EventManager;
import de.noisruker.threading.ThreadManager;
import org.junit.jupiter.api.Test;

public class LibraryTest {

    @Test
    public void someTestMethod() {
        EventManager.getInstance().registerEventListener(SomeEvent.SomeChildEvent.class, event-> System.out.println("1"));

        EventManager.getInstance().registerEventListener(SomeEvent.class, event -> System.out.println("2"));

        ThreadManager.getInstance().executeAsync(() -> {
            EventManager.getInstance().triggerEvent(new SomeEvent.SomeChildEvent(new SomeEvent.SomeClass()));

            EventManager.getInstance().registerEventListener(SomeEvent.SomeChildEvent.class, event-> System.out.println("3"));

            EventManager.getInstance().triggerEvent(new SomeEvent<>("Text", new SomeEvent.SomeClass()));

            EventManager.getInstance().registerEventListener(SomeEvent.class, event -> System.out.println("4"));
            EventManager.getInstance().triggerEvent(new SomeEvent.SomeChildEvent(new SomeEvent.SomeClass()));
            EventManager.getInstance().triggerEvent(new SomeEvent.SomeChildEvent(new SomeEvent.SomeClass()));
        });

        EventManager.getInstance().registerEventListener(SomeEvent.SomeChildEvent.class, event-> System.out.println("5"));

        EventManager.getInstance().triggerEvent(new SomeEvent.SomeChildEvent(new SomeEvent.SomeClass()));

        EventManager.getInstance().triggerEvent(new SomeEvent<>("Text", new SomeEvent.SomeClass()));
    }

}
