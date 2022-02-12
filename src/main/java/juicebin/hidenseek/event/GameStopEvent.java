package juicebin.hidenseek.event;

import juicebin.hidenseek.game.Game;

public class GameStopEvent extends GameEvent {
    public GameStopEvent(Game game) {
        super(game);
    }
}
