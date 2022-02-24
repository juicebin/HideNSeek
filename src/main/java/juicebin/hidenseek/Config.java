package juicebin.hidenseek;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public final class Config {
    private final FileConfiguration config;
    private final int hideTime;
    private final int matchTime;
    private final int borderShrinkStartTime;
    private final int borderShrinkInterval;
    private final double borderShrinkSize;
    private final int glowStartTime;
    private final int glowInterval;
    private final Location lobbyLocation;
    private final Location hiderSpawn;
    private final Location seekerSpawn;
    private final World gameWorld;
    private final List<String> seekerNames;
    private final List<String> hiderNamesAll;
    private final double borderCenterX;
    private final double borderCenterZ;
    private final double borderInitialSize;
    private final long borderShrinkTime;
    private final List<Integer> borderWarningTimes;
    private final List<Integer> matchWarningTimes;
    private final boolean isDebugMode;

    public Config(HideNSeek instance) {
        FileConfiguration config = instance.getConfig();
        FileConfiguration teamConfig = instance.getTeamConfig();

        this.config = config;

        this.hideTime = config.getInt("hide_time");
        this.matchTime = config.getInt("match_time");
        this.glowStartTime = config.getInt("glow.start_time");
        this.glowInterval = config.getInt("glow.interval");
        this.seekerNames = teamConfig.getStringList("seekers");
        this.hiderNamesAll = new ArrayList<>();
        this.lobbyLocation = new Location(
                instance.getServer().getWorld(config.getString("lobby.world")),
                config.getDouble("lobby.x"),
                config.getDouble("lobby.y"),
                config.getDouble("lobby.z")
        );
        this.hiderSpawn = new Location(
                instance.getServer().getWorld(config.getString("game.world")),
                config.getDouble("game.hider_spawn.x"),
                config.getDouble("game.hider_spawn.y"),
                config.getDouble("game.hider_spawn.z")
        );
        this.seekerSpawn = new Location(
                instance.getServer().getWorld(config.getString("game.world")),
                config.getDouble("game.seeker_spawn.x"),
                config.getDouble("game.seeker_spawn.y"),
                config.getDouble("game.seeker_spawn.z")
        );
        this.gameWorld = instance.getServer().getWorld(config.getString("game.world"));
        ConfigurationSection hiders = teamConfig.getConfigurationSection("hiders");
        for (String key : hiders.getKeys(false)) {
            hiderNamesAll.addAll(hiders.getStringList(key));
        }
        this.isDebugMode = config.isBoolean("debug");

        this.borderShrinkStartTime = config.getInt("border.start_shrink_time");
        this.borderShrinkInterval = config.getInt("border.shrink_interval");
        this.borderShrinkSize = config.getDouble("border.shrink_size");
        this.borderCenterX = config.getDouble("border.center.x");
        this.borderCenterZ = config.getDouble("border.center.z");
        this.borderInitialSize = config.getDouble("border.initial_size");
        this.borderWarningTimes = config.getIntegerList("border.warning_times");
        this.borderShrinkTime = config.getLong("border.shrink_time");
        this.matchWarningTimes = config.getIntegerList("match_stop_warning_times");
    }

    public int getHideTime() {
        return hideTime;
    }

    public int getMatchTime() {
        return matchTime;
    }

    public int getGlowStartTime() {
        return glowStartTime;
    }

    public int getGlowInterval() {
        return glowInterval;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public List<String> getSeekerNames() {
        return seekerNames;
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public int getBorderShrinkStartTime() {
        return borderShrinkStartTime;
    }

    public int getBorderShrinkInterval() {
        return borderShrinkInterval;
    }

    public double getBorderShrinkSize() {
        return borderShrinkSize;
    }

    public double getBorderCenterX() {
        return borderCenterX;
    }

    public double getBorderCenterZ() {
        return borderCenterZ;
    }

    public double getBorderInitialSize() {
        return borderInitialSize;
    }

    public List<Integer> getBorderWarningTimes() {
        return borderWarningTimes;
    }

    public List<Integer> getMatchWarningTimes() {
        return matchWarningTimes;
    }

    public Location getHiderSpawn() {
        return hiderSpawn;
    }

    public Location getSeekerSpawn() {
        return seekerSpawn;
    }

    public World getGameWorld() {
        return gameWorld;
    }

    public long getBorderShrinkTime() {
        return borderShrinkTime;
    }
}
