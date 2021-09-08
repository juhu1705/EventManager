package de.noisruker.event.events;

public abstract class Event {

    private final String name;
    private Object result;

    protected Event(String name) {
        this.name = name;
        this.result = null;
    }

    public String getEventName() {
        return this.name;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return this.result;
    }
}
