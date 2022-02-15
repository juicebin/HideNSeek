package juicebin.hidenseek.listener;

import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.*;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

import java.util.logging.Level;

import static juicebin.hidenseek.HideNSeek.debug;

public class GameListener extends RegisteredListener {
    private final HideNSeek plugin;

    public GameListener(HideNSeek instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onGameStartEvent(GameStartEvent event) {
        debug("Event called: GameStartEvent");
    }

    @EventHandler
    public void onGameStopEvent(GameStopEvent event) {
        debug("Event called: GameStopEvent");
    }

    @EventHandler
    public void onHidersGlowEvent(HidersGlowEvent event) {
        debug("Event called: HidersGlowEvent");
    }

    @EventHandler
    public void seekersReleasedEvent(SeekersReleasedEvent event) {
        debug("Event called: SeekersReleasedEvent");
    }

    @EventHandler
    public void onBorderShrinkEvent(BorderShrinkEvent event) {
        debug("Event called: BorderShrinkEvent");
    }

    @EventHandler
    public void onSeekerTagHider(SeekerTagHiderEvent event) {
        debug("Event called: SeekerTagHiderEvent");
    }

}
