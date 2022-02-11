package juicebin.hidenseek.game;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import juicebin.hidenseek.Config;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.BorderShrinkEvent;
import juicebin.hidenseek.event.HidersGlowEvent;
import juicebin.hidenseek.event.SeekersReleasedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;

public final class Game implements Listener {
    private final Location lobbyLocation;
    private final Location hiderSpawn;
    private final Location seekerSpawn;
    private final HideNSeek plugin;
    private boolean active;
    private boolean seekersReleased;
    private boolean borderStartedShrink;
    private boolean hidersStartGlow;
    private int ticks;

    public Game(HideNSeek instance, ProtectedCuboidRegion gameRegion, Location lobbyLocation, Location hiderSpawn, Location seekerSpawn) {
        this.lobbyLocation = lobbyLocation;
        this.hiderSpawn = hiderSpawn;
        this.seekerSpawn = seekerSpawn;
        this.plugin = instance;

        // Initialize region flags
        ConfigurationSection regionFlagSection = instance.getConfig().getConfigurationSection("region_flags");
        HashMap<Flag<?>, Object> flags = new HashMap<>();
        flags.put(Flags.INVINCIBILITY, regionFlagSection.getBoolean("INVINCIBILITY"));
        flags.put(Flags.SLEEP, regionFlagSection.getBoolean("SLEEP"));
        flags.put(Flags.TRAMPLE_BLOCKS, regionFlagSection.getBoolean("TRAMPLE_BLOCKS"));
        flags.put(Flags.ITEM_FRAME_ROTATE, regionFlagSection.getBoolean("ITEM_FRAME_ROTATE"));
        flags.put(Flags.OTHER_EXPLOSION, regionFlagSection.getBoolean("OTHER_EXPLOSION"));
        flags.put(Flags.FIRE_SPREAD, regionFlagSection.getBoolean("FIRE_SPREAD"));
        flags.put(Flags.MOB_SPAWNING, regionFlagSection.getBoolean("MOB_SPAWNING"));
        flags.put(Flags.LAVA_FIRE, regionFlagSection.getBoolean("LAVA_FIRE"));
        flags.put(Flags.ITEM_PICKUP, regionFlagSection.getBoolean("ITEM_PICKUP"));
        flags.put(Flags.ITEM_DROP, regionFlagSection.getBoolean("ITEM_DROP"));
        flags.put(Flags.EXP_DROPS, regionFlagSection.getBoolean("EXP_DROPS"));
        flags.put(Flags.HUNGER_DRAIN, regionFlagSection.getBoolean("HUNGER_DRAIN"));
        gameRegion.setFlags(flags);
    }

    public void start() {
        active = true;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void stop() {
        ticks = 0;
        active = false;
        seekersReleased = false;
        borderStartedShrink = false;
        hidersStartGlow = false;
        HandlerList.unregisterAll(this);
    }

    public void tick() {
        ticks++;

        if (!seekersReleased && ticks >= Config.HIDE_TIME) {
            seekersReleased = true;

            SeekersReleasedEvent event = new SeekersReleasedEvent();
            if (!event.isCancelled()) {
                Bukkit.getPluginManager().callEvent(event);
            }
        } else if (!borderStartedShrink && ticks >= Config.BORDER_SHRINK_START_TIME) {
            borderStartedShrink = true;

            BorderShrinkEvent event = new BorderShrinkEvent(true);
            if (!event.isCancelled()) {
                Bukkit.getPluginManager().callEvent(event);
            }
        } else if (!hidersStartGlow && ticks >= Config.GLOW_START_TIME) {
            hidersStartGlow = true;

            HidersGlowEvent event = new HidersGlowEvent(true);
            if (!event.isCancelled()) {
                Bukkit.getPluginManager().callEvent(event);
            }
        }

        if (ticks >= Config.SEEK_TIME) {
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

    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        tick();
    }

}
