package juicebin.hidenseek;

import org.bukkit.configuration.file.FileConfiguration;

public final class ManagedConfig {
    private static final FileConfiguration CONFIG = HideNSeek.INSTANCE.getConfig();
    private static final int HIDE_TIME = CONFIG.getInt("hide_time");
    private static final int SEEK_TIME = CONFIG.getInt("seek_time");
    private static final int BORDER_SHRINK_START_TIME = CONFIG.getInt("border_start_shrink");
    private static final int BORDER_SHRINK_INTERVAL = CONFIG.getInt("border_shrink_interval");
    private static final double BORDER_SHRINK_SIZE = CONFIG.getDouble("border_shrink_size");
    private static final int GLOW_START_TIME = CONFIG.getInt("glow_start");
    private static final int GLOW_INTERVAL = CONFIG.getInt("glow_interval");

    // get region flags

    // get seekers

    // get hiding teams

    // get specific hiding team

}
