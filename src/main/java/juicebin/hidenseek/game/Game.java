package juicebin.hidenseek.game;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import juicebin.hidenseek.HideNSeek;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public final class Game {
    private final ProtectedCuboidRegion gameRegion;
    private final Location lobbyLocation;
    private final Location hiderSpawn;
    private final Location seekerSpawn;
    private boolean active;
    private int ticks;

    public Game(HideNSeek instance, ProtectedCuboidRegion gameRegion, Location lobbyLocation, Location hiderSpawn, Location seekerSpawn) {
        this.gameRegion = gameRegion;
        this.lobbyLocation = lobbyLocation;
        this.hiderSpawn = hiderSpawn;
        this.seekerSpawn = seekerSpawn;

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
        this.active = true;
    }

    public void stop() {
        ticks = 0;
    }

    public void tick() {
        ticks++;

        if (ticks <= 0) {
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
}
