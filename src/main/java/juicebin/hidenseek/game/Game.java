package juicebin.hidenseek.game;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import juicebin.hidenseek.Config;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.*;
import juicebin.hidenseek.util.ScoreHelper;
import juicebin.hidenseek.util.TickUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static juicebin.hidenseek.HideNSeek.log;

public class Game implements Listener {
    private static final TextComponent title = Component.text()
            .append(Component.text("Ikea").color(TextColor.color(235, 192, 0)))
            .append(Component.space())
            .append(Component.text("Hide N Seek").color(TextColor.color(25, 44, 255)))
            .build();
    private final List<UUID> taggedPlayers = new ArrayList<>();
    private final HideNSeek plugin;
    private final World world;
    private final String id;
    private final Location lobbyLocation;
    private final Location hiderSpawn;
    private final Location seekerSpawn;
    private final Scoreboard scoreboard;
    private final Config config;
    private final int matchTime;
    private boolean active;
    private boolean seekersReleased;
    private boolean borderStartedShrink;
    private boolean hidersStartGlow;
    private int ticks;

    public Game(HideNSeek instance, String id, World world, Location lobbyLocation, Location hiderSpawn, Location seekerSpawn) {
        this.plugin = instance;
        this.id = id;
        this.world = world;
        this.lobbyLocation = lobbyLocation;
        this.hiderSpawn = hiderSpawn;
        this.seekerSpawn = seekerSpawn;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.config = plugin.getConfigInstance();
        this.matchTime = plugin.getConfigInstance().getMatchTime();

        FileConfiguration teamConfig = instance.getTeamConfig();

        // Initialize hiding teams
        ConfigurationSection hiders = teamConfig.getConfigurationSection("hiders");
        for (String key : hiders.getKeys(false)) {
            String teamName = "hiders-" + key;
            Team team = this.scoreboard.registerNewTeam(teamName);
            key = "hiders." + key;
            NamedTextColor color = NamedTextColor.NAMES.value(teamConfig.getString(key + ".color"));
            team.displayName(Component.text(teamConfig.getString(key + ".display-name")));
            team.color(color);
            team.addEntries(hiders.getStringList(key + ".players"));
        }

        // Initialize seeking team
        Team team = this.scoreboard.registerNewTeam("seekers");
        NamedTextColor color = NamedTextColor.NAMES.value(teamConfig.getString("seekers.color"));
        team.displayName(Component.text(teamConfig.getString("seekers.display-name")));
        team.color(color);
        team.addEntries(teamConfig.getStringList("seekers.players"));
    }

    public void start() {
        ticks = config.getMatchTime();
        active = true;
        seekersReleased = false;
        borderStartedShrink = false;
        hidersStartGlow = false;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));

        // Show scoreboard
        for (Player player : this.getPlayers()) {
            ScoreHelper scoreHelper = ScoreHelper.createScore(player);
            Team team = this.getTeam(player);
            scoreHelper.setTitle(title);
            scoreHelper.setSlot(2, "&fTeam: " + team.getName()); // TODO: team color
        }

        log(Level.INFO, "Starting registered game \"" + id + "\"...");
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().callEvent(new GameStopEvent(this));

        ticks = 0;
        active = false;
        seekersReleased = false;
        borderStartedShrink = false;
        hidersStartGlow = false;

        // Hide scoreboard
        for (Player player : this.getPlayers()) {
            ScoreHelper.removeScore(player);
        }

        log(Level.INFO, "Stopping registered game \"" + id + "\"...");
    }

    public void tick() {
        ticks--;

        if (!this.active) return;

        for (Player player : this.getPlayers()) {
            ScoreHelper scoreHelper = ScoreHelper.getByPlayer(player);
            scoreHelper.setSlot(1, "&cTime Left: &r" + TickUtils.convertTicksToTime(ticks));
        }

        if (!seekersReleased && ticks <= config.getHideTime()) {
            seekersReleased = true;

            SeekersReleasedEvent event = new SeekersReleasedEvent(this);
            if (!event.isCancelled()) {
                Bukkit.getPluginManager().callEvent(event);
            }
        }

        if (ticks <= config.getBorderShrinkStartTime()) {
            if (!borderStartedShrink) {
                borderStartedShrink = true;

                BorderShrinkEvent event = new BorderShrinkEvent(this, true);
                if (!event.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(event);
                }
            } else if ((ticks - (matchTime - config.getBorderShrinkStartTime())) % config.getBorderShrinkInterval() == 0) {
                BorderShrinkEvent event = new BorderShrinkEvent(this, false);
                if (!event.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }

        if (ticks <= config.getGlowStartTime()) {
            if (!hidersStartGlow) {
                hidersStartGlow = true;

                HidersGlowEvent event = new HidersGlowEvent(this, true);
                if (!event.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(event);
                }
            } else if ((ticks - (matchTime - config.getGlowStartTime())) % config.getGlowInterval() == 0) {
                HidersGlowEvent event = new HidersGlowEvent(this, true);
                if (!event.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }

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

    public String getId() {
        return id;
    }

    public World getWorld() {
        return world;
    }

    public boolean isTagged(Player player) {
        return taggedPlayers.contains(player.getUniqueId());
    }

    public void setTagged(Player player, boolean tagged) {
        if (tagged && !isTagged(player)) {
            taggedPlayers.add(player.getUniqueId());
        } else if (!tagged & isTagged(player)) {
            taggedPlayers.remove(player.getUniqueId());
        }
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

    public Set<Team> getTeams() {
        return this.scoreboard.getTeams();
    }

    public Team getSeekingTeam() {
        return this.getTeam("seekers");
    }

    public Set<Team> getHidingTeams() {
        return this.getTeams().stream().filter(t -> t.getName().startsWith("hider-")).collect(Collectors.toSet());
    }

    public Team getTeam(String teamName) {
        return this.scoreboard.getTeam(teamName);
    }

    public Team getTeam(OfflinePlayer player) {
        return this.scoreboard.getPlayerTeam(player);
    }

    public void addTeam(String teamName, NamedTextColor color, Component prefix) {
        if (this.scoreboard.getTeam(teamName) != null) {
            // TODO: Send error
            return;
        }

        Team team = this.scoreboard.registerNewTeam(teamName);
        team.color(color);
        team.prefix(prefix);
    }

    public void removeTeam(String teamName) {
        Team team = this.scoreboard.getTeam(teamName);
        if (team != null) {
            team.unregister();
        } else {
            // TODO: Send error
        }
    }

    public List<Player> getPlayers() {
        return this.world.getPlayers();
    }

    public void teleportHiders(Location location) {
        for (Team team : this.getHidingTeams()) {
            for (String entry : team.getEntries()) {
                Player player = Bukkit.getPlayer(entry);
                if (player == null) {
                    log(Level.WARNING, "Tried to teleport non-player entry from the HIDERS team. Skipping...");
                    continue;
                }
                player.teleport(location);
            }
        }
    }

    public void teleportSeekers(Location location) {
        for (String entry : this.getSeekingTeam().getEntries()) {
            Player player = Bukkit.getPlayer(entry);
            if (player == null) {
                log(Level.WARNING, "Tried to teleport non-player entry from the SEEKERS team. Skipping...");
                continue;
            }
            player.teleport(location);
        }
    }

    public void initWorldBorder() {
        WorldBorder border = this.world.getWorldBorder();
        border.setCenter(this.config.getBorderCenterX(), this.config.getBorderCenterZ());
        border.setSize(this.config.getBorderInitialSize());
    }

    public void setWorldBorderSize(double size) {
        this.world.getWorldBorder().setSize(size);
    }

    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        this.tick();
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        Entity targetEntity = event.getEntity();
        Entity attackerEntity = event.getDamager();

        // Only continue if the worlds are the same, ensuring it's a part of the game
        if (targetEntity.getWorld() != this.world) return;

        if (targetEntity instanceof Player target && attackerEntity instanceof Player attacker) {
            if (this.isSeeker(attacker) && this.isHider(target)) {
                // If a seeker attacks a hider

                this.setTagged(target, true);

                SeekerTagHiderEvent eventToCall = new SeekerTagHiderEvent(this, attacker, target);
                if (!eventToCall.isCancelled()) {
                    Bukkit.getPluginManager().callEvent(eventToCall);
                }
            }

            // Cancel player vs player damage (disable damage in the world while the game the running)
            event.setCancelled(true);
        }
    }
}
