package juicebin.hidenseek.game;

import java.util.HashMap;
import java.util.Map;

// handle multiple games functioning???
public final class GameHandler {
    public static Map<String, Game> gameMap = new HashMap<>();

    public static void startGame(Game game) {
        gameMap.put(game.getId(), game);
        game.start();
    }

    public static void stopGame(String id) {
        gameMap.get(id).stop();
    }
}
