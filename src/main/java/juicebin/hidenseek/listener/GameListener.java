package juicebin.hidenseek.listener;

import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.*;
import juicebin.hidenseek.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;

import java.util.logging.Level;

import static juicebin.hidenseek.HideNSeek.log;

public class GameListener extends RegisteredListener {
    private final HideNSeek plugin;

    public GameListener(HideNSeek instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onGameStartEvent(GameStartEvent event) {
        log(Level.INFO, "Event called: GameStartEvent");

        Game game = event.getGame();
        World world = game.getWorld();
    }

    @EventHandler
    public void onGameStopEvent(GameStopEvent event) {
        log(Level.INFO, "Event called: GameStopEvent");

        // TODO: Send to lobby and do other game end stuff like saving scores and whatnot

        Game game = event.getGame();
        World world = game.getWorld();
    }

    @EventHandler
    public void onHidersGlowEvent(HidersGlowEvent event) {
        log(Level.INFO, "Event called: HidersGlowEvent");

        Game game = event.getGame();
        World world = game.getWorld();
    }

    @EventHandler
    public void seekersReleasedEvent(SeekersReleasedEvent event) {
        log(Level.INFO, "Event called: SeekersReleasedEvent");

        Game game = event.getGame();
        World world = game.getWorld();
    }

    @EventHandler
    public void onBorderShrinkEvent(BorderShrinkEvent event) {
        log(Level.INFO, "Event called: BorderShrinkEvent");

        Game game = event.getGame();
        World world = game.getWorld();
    }

    @EventHandler
    public void onSeekerTagHider(SeekerTagHiderEvent event) {
        log(Level.INFO, "Event called: SeekerTagHiderEvent");

        Game game = event.getGame();
    }

}
