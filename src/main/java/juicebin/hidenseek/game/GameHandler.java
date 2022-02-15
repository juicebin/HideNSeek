package juicebin.hidenseek.game;

import juicebin.hidenseek.HideNSeek;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

// handle multiple games functioning???
public final class GameHandler {
    private final Map<String, Game> gameMap = new HashMap<>();

    public void registerGame(Game game) {
        String id = game.getId();
        if (this.gameMap.containsKey(id)) {
            Bukkit.getLogger().log(Level.WARNING, "Game \"" + id + "\" is already registered!");
        } else {
            this.gameMap.put(id, game);
            Bukkit.getLogger().log(Level.INFO, "Game \"" + id + "\" successfully registered");
        }
    }

    public Game getGame(String string) {
        return this.gameMap.get(string);
    }

    public List<Game> getGames() {
        return this.gameMap.values().stream().toList();
    }
}
