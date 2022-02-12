package juicebin.hidenseek;

import juicebin.hidenseek.command.Commands;
import juicebin.hidenseek.listener.GameListener;
import juicebin.hidenseek.listener.Listeners;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class HideNSeek extends JavaPlugin {
    public static HideNSeek INSTANCE;
    private Config configInstance;
    private FileConfiguration teamConfig;
    private Commodore commodore;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.saveDefaultConfig();
        this.createTeamsConfig();

        if (configInstance.isDebugMode()) {
            new DebugLoggingProvider().enableDebugLogging();
        }

        this.configInstance = new Config(this);
        this.commodore = CommodoreProvider.getCommodore(this);

        // Register listeners
        Listeners listeners = new Listeners(
                new GameListener(this)
        );
        listeners.register(this);

        // Register commands
        Commands commands = new Commands();
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
}
