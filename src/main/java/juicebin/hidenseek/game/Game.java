package juicebin.hidenseek.game;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import juicebin.hidenseek.Config;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class Game implements Listener {
    private final HideNSeek plugin;
    private final World world;
    private final String id;
    private final Location lobbyLocation;
    private final Location hiderSpawn;
    private final Location seekerSpawn;
    private boolean active;
    private boolean seekersReleased;
    private boolean borderStartedShrink;
    private boolean hidersStartGlow;
    private int ticks;

    public Game(HideNSeek instance, String id, World world, Location lobbyLocation, Location hiderSpawn, Location seekerSpawn) {
        this.plugin = instance;
        this.id = id;
        this.world = world;
        this.lobbyLocation = lobbyLocation;
        this.hiderSpawn = hiderSpawn;
        this.seekerSpawn = seekerSpawn;
    }

    protected void start() {
        ticks = 0;
        active = true;
        seekersReleased = false;
        borderStartedShrink = false;
        hidersStartGlow = false;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));
    }

    protected void stop() {
        ticks = 0;
        active = false;
        seekersReleased = false;
        borderStartedShrink = false;
        hidersStartGlow = false;
        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().callEvent(new GameStopEvent(this));
    }

    public void tick() {
        ticks++;

        Config config = plugin.getConfigInstance();
        Objective objective = plugin.getScoreboard().getObjective("time");

        if (objective != null) {
            Score score = objective.getScore("");
        }

        if (!seekersReleased && ticks >= config.getHideTime()) {
            seekersReleased = true;

            SeekersReleasedEvent event = new SeekersReleasedEvent(this);
            if (!event.isCancelled()) {
                Bukkit.getPluginManager().callEvent(event);
            }
        }

        if (ticks >= config.getBorderShrinkStartTime()) {
            if (!borderStartedShrink) {
                borderStartedShrink = true;

                BorderShrinkEvent event = new BorderShrinkEvent(this, true);
                if (!event.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(event);
                }
            } else if ((ticks - config.getBorderShrinkStartTime()) % config.getBorderShrinkInterval() == 0) {
                BorderShrinkEvent event = new BorderShrinkEvent(this, false);
                if (!event.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }

        if (ticks >= config.getGlowStartTime()) {
            if (!hidersStartGlow) {
                hidersStartGlow = true;

                HidersGlowEvent event = new HidersGlowEvent(this, true);
                if (!event.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(event);
                }
            } else if ((ticks - config.getGlowStartTime()) % config.getGlowInterval() == 0) {
                HidersGlowEvent event = new HidersGlowEvent(this, true);
                if (!event.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }

        if (ticks >= config.getSeekTime()) {
            this.stop();
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public Location getHiderSpawn() {
        return hiderSpawn;
    }

    public Location getSeekerSpawn() {
        return seekerSpawn;
    }

    public String getId() {
        return id;
    }

    public World getWorld() {
        return world;
    }

    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        tick();
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        Entity targetEntity = event.getEntity();
        Entity attackerEntity = event.getDamager();

        // Only continue if the worlds are the same, ensuring it's a part of the game
        if (targetEntity.getWorld() != this.world) return;

        if (targetEntity instanceof Player target && attackerEntity instanceof Player attacker) {
            if (plugin.isSeeker(attacker) && plugin.isHider(target)) {
                // If a seeker attacks a hider
                SeekerTagHiderEvent eventToCall = new SeekerTagHiderEvent(this, attacker, target);
                if (!eventToCall.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(eventToCall);
                }
            }

            // Cancel player vs player damage (disable damage in the world while the game the running)
            event.setCancelled(true);
        }
    }
}
