package juicebin.hidenseek;

import org.bukkit.configuration.file.FileConfiguration;

public final class Config {
    private static final FileConfiguration CONFIG = HideNSeek.INSTANCE.getConfig();
    public static final int HIDE_TIME = CONFIG.getInt("hide_time");
    public static final int SEEK_TIME = CONFIG.getInt("seek_time");
    public static final int BORDER_SHRINK_START_TIME = CONFIG.getInt("border_start_shrink");
    public static final int BORDER_SHRINK_INTERVAL = CONFIG.getInt("border_shrink_interval");
    public static final double BORDER_SHRINK_SIZE = CONFIG.getDouble("border_shrink_size");
    public static final int GLOW_START_TIME = CONFIG.getInt("glow_start");
    public static final int GLOW_INTERVAL = CONFIG.getInt("glow_interval");


    // get region flags

    // get seekers

    // get hiding teams

    // get specific hiding team

}
