package juicebin.hidenseek;

import org.bukkit.Location;
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
    private final List<String> seekerNames;
    private final List<String> hiderNamesAll;
    private final double borderCenterX;
    private final double borderCenterZ;
    private final double borderInitialSize;
    private final List<Integer> borderWarningTimes;
    private final List<Integer> matchWarningTimes;
    private final boolean isDebugMode;

    public Config(HideNSeek instance) {
        FileConfiguration config = instance.getConfig();
        FileConfiguration teamConfig = instance.getTeamConfig();

        this.config = config;

        this.hideTime = config.getInt("hide_time");
        this.matchTime = config.getInt("match_time");
        this.glowStartTime = config.getInt("glow_start");
        this.glowInterval = config.getInt("glow_interval");
        this.seekerNames = teamConfig.getStringList("seekers");
        this.hiderNamesAll = new ArrayList<>();
        this.lobbyLocation = new Location(
                instance.getServer().getWorld(config.getString("lobby.world")),
                config.getDouble("lobby.x"),
                config.getDouble("lobby.y"),
                config.getDouble("lobby.z")
        );
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
        this.matchWarningTimes = config.getIntegerList("match_stop_warning_times");
    }

    public List<String> getHiderTeam(String id) {
        return config.getConfigurationSection("hiders").getStringList(id);
    }

    public int getHideTime() {
        return hideTime;
    }

    public int getMatchTime() {
        return matchTime;
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
}
