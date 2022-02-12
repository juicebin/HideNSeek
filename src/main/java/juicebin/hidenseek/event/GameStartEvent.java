package juicebin.hidenseek.event;

import juicebin.hidenseek.game.Game;

public class GameStartEvent extends GameEvent {
    public GameStartEvent(Game game) {
        super(game);
    }
}
