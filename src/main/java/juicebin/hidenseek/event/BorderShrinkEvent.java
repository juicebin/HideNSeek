package juicebin.hidenseek.event;

import juicebin.hidenseek.game.Game;

public class BorderShrinkEvent extends GameEvent {
    private final boolean firstEvent;

    public BorderShrinkEvent(Game game, boolean firstEvent) {
        super(game);
        this.firstEvent = firstEvent;
    }

    public boolean isFirstEvent() {
        return this.firstEvent;
    }
}
