package juicebin.hidenseek.event;

import juicebin.hidenseek.game.Game;

public class HidersGlowEvent extends GameEvent {
    private final boolean firstEvent;

    public HidersGlowEvent(Game game, boolean firstEvent) {
        super(game);
        this.firstEvent = firstEvent;
    }

    public boolean isFirstEvent() {
        return this.firstEvent;
    }
}
