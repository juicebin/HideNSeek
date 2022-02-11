package juicebin.hidenseek.game;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

// handle multiple games functioning???
public final class GameHandler {
    public static Map<String, Game> gameMap = new HashMap<>();

    public static void registerGame(Game game) {
        String id = game.getId();
        if (gameMap.containsKey(id)) {
            Bukkit.getLogger().log(Level.WARNING, "Game \"" + id + "\" is already registered!");
        } else {
            gameMap.put(id, game);
            Bukkit.getLogger().log(Level.INFO, "Game \"" + id + "\" successfully registered");
        }
    }

    public static void startGame(String id) {
        if (!gameMap.containsKey(id)){
            Bukkit.getLogger().log(Level.INFO, "Starting registered game \"" + id + "\"...");
        }

        gameMap.get(id).start();
    }

    public static void stopGame(String id) {
        Bukkit.getLogger().log(Level.INFO, "Stopping registered game \"" + id + "\"...");
        gameMap.get(id).stop();
    }
}
