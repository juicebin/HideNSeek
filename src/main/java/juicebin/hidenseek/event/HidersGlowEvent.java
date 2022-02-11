package juicebin.hidenseek.event;

public class HidersGlowEvent extends GameEvent {
    private final boolean firstEvent;

    public HidersGlowEvent(boolean firstEvent) {
        this.firstEvent = firstEvent;
    }

    public boolean isFirstEvent() {
        return this.firstEvent;
    }
}
