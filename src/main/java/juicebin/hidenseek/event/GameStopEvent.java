package juicebin.hidenseek.event;

import juicebin.hidenseek.game.Game;

public class GameStopEvent extends GameEvent {
    private boolean forced;

    public GameStopEvent(Game game, boolean forced) {
        super(game);
        this.forced = forced;
    }

    public boolean isForced() {
        return forced;
    }
}
