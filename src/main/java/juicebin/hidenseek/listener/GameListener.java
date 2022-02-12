package juicebin.hidenseek.listener;

import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.logging.Level;

public class GameListener extends RegisteredListener {
    private HideNSeek plugin;

    public GameListener(HideNSeek instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onGameStartEvent(GameStartEvent event) {
        Bukkit.getLogger().log(Level.CONFIG, "Event called: GameStartEvent");
    }

    @EventHandler
    public void onGameStopEvent(GameStopEvent event) {
        Bukkit.getLogger().log(Level.CONFIG, "Event called: GameStopEvent");
    }

    @EventHandler
    public void onHidersGlowEvent(HidersGlowEvent event) {
        Bukkit.getLogger().log(Level.CONFIG, "Event called: HidersGlowEvent");
    }

    @EventHandler
    public void seekersReleasedEvent(SeekersReleasedEvent event) {
        Bukkit.getLogger().log(Level.CONFIG, "Event called: SeekersReleasedEvent");
    }

    @EventHandler
    public void onBorderShrinkEvent(BorderShrinkEvent event) {
        Bukkit.getLogger().log(Level.CONFIG, "Event called: BorderShrinkEvent");
    }

    @EventHandler
    public void onSeekerTagHider(SeekerTagHiderEvent event) {

    }

}
