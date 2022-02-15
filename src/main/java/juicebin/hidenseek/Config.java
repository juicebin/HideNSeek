package juicebin.hidenseek;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Config {
    private final FileConfiguration config;
    private final int hideTime;
    private final int seekTime;
    private final int borderShrinkStartTime;
    private final int borderShrinkInterval;
    private final double borderShrinkSize;
    private final int glowStartTime;
    private final int glowInterval;
    private final Location lobbyLocation;
    private final List<String> seekerNames;
    private final List<String> hiderNamesAll;
    private final boolean isDebugMode;

    public Config(HideNSeek instance) {
        FileConfiguration config = instance.getConfig();
        FileConfiguration teamConfig = instance.getTeamConfig();

        this.config = config;

        this.hideTime = config.getInt("hide_time");
        this.seekTime = config.getInt("seek_time");
        this.borderShrinkStartTime = config.getInt("border_start_shrink");
        this.borderShrinkInterval = config.getInt("border_shrink_interval");
        this.borderShrinkSize = config.getDouble("border_shrink_size");
        this.glowStartTime = config.getInt("glow_start");
        this.glowInterval = config.getInt("glow_interval");
        Map<String, Boolean> regionFlags1 = new HashMap<>();
        this.seekerNames = teamConfig.getStringList("seekers");
        this.hiderNamesAll = new ArrayList<>();
        this.isDebugMode = config.isBoolean("debug");
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
    }

    public List<String> getHiderTeam(String id) {
        return config.getConfigurationSection("hiders").getStringList(id);
    }

    public int getHideTime() {
        return hideTime;
    }

    public int getSeekTime() {
        return seekTime;
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
}
