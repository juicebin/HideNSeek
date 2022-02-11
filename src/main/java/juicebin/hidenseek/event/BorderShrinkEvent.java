package juicebin.hidenseek.event;

public class BorderShrinkEvent extends GameEvent {
    private final boolean firstEvent;

    public BorderShrinkEvent(boolean firstEvent) {
        this.firstEvent = firstEvent;
    }

    public boolean isFirstEvent() {
        return this.firstEvent;
    }
}
