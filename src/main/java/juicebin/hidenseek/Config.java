package juicebin.hidenseek;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Config {
    private static final FileConfiguration CONFIG = HideNSeek.INSTANCE.getConfig();
    private static final FileConfiguration TEAM_CONFIG = HideNSeek.INSTANCE.getTeamConfig();
    public static final int HIDE_TIME = CONFIG.getInt("hide_time");
    public static final int SEEK_TIME = CONFIG.getInt("seek_time");
    public static final int BORDER_SHRINK_START_TIME = CONFIG.getInt("border_start_shrink");
    public static final int BORDER_SHRINK_INTERVAL = CONFIG.getInt("border_shrink_interval");
    public static final double BORDER_SHRINK_SIZE = CONFIG.getDouble("border_shrink_size");
    public static final int GLOW_START_TIME = CONFIG.getInt("glow_start");
    public static final int GLOW_INTERVAL = CONFIG.getInt("glow_interval");
    public static final Map<String, Boolean> REGION_FLAGS = new HashMap<>();
    public static final List<String> SEEKER_NAMES = TEAM_CONFIG.getStringList("seekers");
    public static final List<String> HIDER_NAMES_ALL = new ArrayList<>();

    static {
        ConfigurationSection regionFlags = CONFIG.getConfigurationSection("region_flags");
        for (String key : regionFlags.getKeys(false)) {
            REGION_FLAGS.put(key, regionFlags.getBoolean(key));
        }

        ConfigurationSection hiders = TEAM_CONFIG.getConfigurationSection("hiders");
        for (String key : hiders.getKeys(false)) {
            HIDER_NAMES_ALL.addAll(hiders.getStringList(key));
        }
    }

    public static List<String> getHiderTeam(String id) {
        return CONFIG.getConfigurationSection("hiders").getStringList(id);
    }
}
