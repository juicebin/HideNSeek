package juicebin.hidenseek;

import juicebin.hidenseek.command.Commands;
import juicebin.hidenseek.command.GameCommand;
import juicebin.hidenseek.game.GameHandler;
import juicebin.hidenseek.listener.GameListener;
import juicebin.hidenseek.listener.Listeners;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class HideNSeek extends JavaPlugin {
    public static HideNSeek INSTANCE;
    private static final String LOG_PREFIX = "[HideNSeek] ";
    private Config configInstance;
    private FileConfiguration teamConfig;
    private Commodore commodore;
    private GameHandler gameHandler;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.saveDefaultConfig();
        this.createTeamsConfig();

        this.configInstance = new Config(this);
        this.commodore = CommodoreProvider.getCommodore(this);
        this.gameHandler = new GameHandler();

        // Register listeners
        Listeners listeners = new Listeners(
                new GameListener(this)
        );
        listeners.register(this);

        // Register commands
        Commands commands = new Commands(
                new GameCommand()
        );
        commands.register(this);

    }

    @Override
    public void onDisable() {

    }

    public Commodore getCommodore() {
        return commodore;
    }

    private void createTeamsConfig() {
        File teamConfigFile = new File(getDataFolder(), "teams.yml");
        if (!teamConfigFile.exists()) {
            teamConfigFile.getParentFile().mkdirs();
            saveResource("teams.yml", false);
        }

        teamConfig = new YamlConfiguration();
        try {
            teamConfig.load(teamConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getTeamConfig() {
        return teamConfig;
    }

    public Config getConfigInstance() {
        return this.configInstance;
    }

    public void debug(String msg) {
        if (this.getConfigInstance().isDebugMode()) {
            log(Level.CONFIG, msg);
        }
    }

    public static void log(Level level, String msg) {
        Bukkit.getLogger().log(level, LOG_PREFIX + msg);
    }

    public GameHandler getGameHandler() {
        return gameHandler;
    }

    public static TextComponent getPrefix() {
        return Component.text()
                .style(Style.style(TextDecoration.BOLD))
                .append(Component.text("[").color(NamedTextColor.DARK_GRAY))
                .append(Component.text("H").color(NamedTextColor.BLUE))
                .append(Component.text("&").color(NamedTextColor.GRAY))
                .append(Component.text("S").color(NamedTextColor.YELLOW))
                .append(Component.text("]").color(NamedTextColor.DARK_GRAY))
                .build().append(Component.text().style(Style.empty()));
    }
}
