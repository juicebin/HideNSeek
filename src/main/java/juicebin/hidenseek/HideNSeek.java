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
    private Scoreboard scoreboard;

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
        this.scoreboard = this.getServer().getScoreboardManager().getNewScoreboard();

        // Register listeners
        Listeners listeners = new Listeners(
                new GameListener(this)
        );
        listeners.register(this);

        // Register commands
        Commands commands = new Commands();
        commands.register(this);

        // Initialize hiding teams
        ConfigurationSection hiders = teamConfig.getConfigurationSection("hiders");
        for (String key : hiders.getKeys(false)) {
            String teamName = "hiders-" + key;
            this.scoreboard.registerNewTeam(teamName);
            Team team = this.scoreboard.getTeam(key);
            if (team == null) {
                // TODO: SEND ERROR
                return;
            }
            team.addEntries(hiders.getStringList(key));
        }

        // Initialize seeking team
        this.scoreboard.registerNewTeam("seekers");
        Team team = this.scoreboard.getTeam("seekers");
        if (team == null) {
            // TODO: SEND ERROR
            return;
        }
        team.addEntries(teamConfig.getStringList("seekers"));

        // Initialize scoreboard objectives
        Objective objective = this.scoreboard.registerNewObjective("time", "dummy", Component.text("Time"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
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

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public boolean isSeeker(Player player) {
        Team seekers = this.scoreboard.getTeam("seekers");
        if (seekers == null) {
            // TODO: Send error
            return false;
        }
        return seekers.hasPlayer(player);
    }

    public boolean isHider(Player player) {
        List<Set<String>> entryList = this.scoreboard.getTeams()
                .stream()
                .filter(t -> t.getName().startsWith("hiders-"))
                .map(Team::getEntries)
                .collect(Collectors.toList());

        for (Set<String> strings : entryList) {
            if (strings.contains(player.getName())) {
                return true;
            }
        }
        return false;
    }

    public Config getConfigInstance() {
        return this.configInstance;
    }
}
