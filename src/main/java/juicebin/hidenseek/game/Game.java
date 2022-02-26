package juicebin.hidenseek.game;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import juicebin.hidenseek.Config;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.*;
import juicebin.hidenseek.util.MessageUtils;
import juicebin.hidenseek.util.ScoreHelper;
import juicebin.hidenseek.util.SoundUtils;
import juicebin.hidenseek.util.TickUtils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
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

import java.util.*;
import java.util.logging.Level;

import static juicebin.hidenseek.HideNSeek.log;

public final class Game implements Listener {
    private final List<UUID> taggedPlayers = new ArrayList<>();
    private final Map<String, HidingTeam> hidingTeamMap = new HashMap<>();
    private final SeekingTeam seekingTeam;
    private final HideNSeek plugin;
    private final World world;
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
    private int teamCount;
    private int ticks;

    public Game(HideNSeek instance, World world, Location lobbyLocation, Location hiderSpawn, Location seekerSpawn) {
        this.plugin = instance;
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
            ConfigurationSection teamSection = hiders.getConfigurationSection(key);
            String displayName = teamSection.getString("display-name");
            TextColor color = TextColor.color(teamSection.getInt("color"));
            ConfigurationSection players = teamSection.getConfigurationSection("players");

            HidingTeam team = new HidingTeam(key, displayName, color);
            for (String playerName : players.getKeys(false)) {
                UUID uuid = UUID.fromString(players.getString(playerName));
                team.addPlayer(uuid);
            }

            hidingTeamMap.put(key, team);
        }

        // Initialize seeking team
        ConfigurationSection seekers = teamConfig.getConfigurationSection("seekers");
        String displayName = seekers.getString("display-name");
        TextColor color = TextColor.color(seekers.getInt("color"));
        ConfigurationSection players = seekers.getConfigurationSection("players");

        SeekingTeam team = new SeekingTeam("seekers", displayName, color);
        for (String playerName : players.getKeys(false)) {
            UUID uuid = UUID.fromString(players.getString(playerName));
            team.addPlayer(uuid);
        }

        this.seekingTeam = team;

        // Add scoreboard teams (solely for nameplates)
        for (HidingTeam hidingTeam : hidingTeamMap.values()) {
            Team scoreboardTeam = this.scoreboard.registerNewTeam(hidingTeam.getId());
            hidingTeam.getOfflinePlayers().forEach(scoreboardTeam::addPlayer);
        }
        Team scoreboardTeam = this.scoreboard.registerNewTeam(seekingTeam.getId());
        seekingTeam.getOfflinePlayers().forEach(scoreboardTeam::addPlayer);
    }

    public void start() {
        ticks = config.getMatchTime();
        active = true;

        this.initWorldBorder();

        // Untag players and set all teams to active
        for (HidingTeam team : this.getHidingTeams()) {
            for (OfflinePlayer player : team.getOfflinePlayers()) {
                team.setPlayerActive(player, false);
            }
            team.setActive(true);
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));

        // Hide nameplates for other teams
        for (Team team : this.scoreboard.getTeams()) {
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        }

        log(Level.INFO, "Starting registered game...");
    }

    public void stop(boolean forced) {
        HandlerList.unregisterAll(this);
        Bukkit.getPluginManager().callEvent(new GameStopEvent(this, forced));

        ticks = 0;
        active = false;
        seekersReleased = false;
        borderStartedShrink = false;
        hidersStartGlow = false;

        this.resetWorldBorder();

        // Show nameplates
        for (Team team : this.scoreboard.getTeams()) {
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        }

        log(Level.INFO, "Stopping registered game...");
    }

    public void tick() {
        ticks--;

        if (!this.active) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
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

        for (int time : config.getBorderWarningTimes()) {
            if (ticks == this.config.getBorderShrinkStartTime() + time) {
                this.sendWarningAlert("Border starting to shrink in", time);
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

        for (int time : config.getGlowWarningTimes()) {
            if (ticks == this.config.getGlowStartTime() + time) {
                this.sendWarningAlert("Hiders starting to glow in", time);
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

        for (int time : config.getMatchWarningTimes()) {
            if (ticks == time) {
                this.sendWarningAlert("Game is ending in", time);
            }
        }

        if (ticks <= 0 || this.getActiveHiders().size() <= 0) {
            this.stop(false);
        }
    }

    private void sendWarningAlert(String text, int timeLeft) {
        int seconds = timeLeft / 20;
        int minutes = seconds / 60;

        String time;

        if (minutes > 1) {
            time = minutes + " minutes";
        } else {
            time = seconds + " seconds";
        }

        MessageUtils.broadcast(Component.text()
                .append(Component.text(text).color(NamedTextColor.RED))
                .append(Component.space())
                .append(Component.text(time).color(NamedTextColor.YELLOW))
                .build());

        SoundUtils.broadcastSound(Sound.sound(org.bukkit.Sound.BLOCK_DISPENSER_DISPENSE, Sound.Source.MASTER, 0.5f, 1.0f));
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
        return this.seekingTeam.hasPlayer(player.getUniqueId());
    }

    public boolean isHider(Player player) {
        for (HidingTeam team : this.getHidingTeams()) {
            if (team.hasPlayer(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public SeekingTeam getSeekingTeam() {
        return seekingTeam;
    }

    public List<HidingTeam> getHidingTeams() {
        return hidingTeamMap.values().stream().toList();
    }

    public AbstractTeam getTeam(String teamName) {
        if (Objects.equals(teamName, "seekers")) {
            return this.getSeekingTeam();
        } else {
            return hidingTeamMap.get(teamName);
        }
    }

    public AbstractTeam getTeam(UUID uuid) {
        if (this.seekingTeam.hasPlayer(uuid)) {
            return seekingTeam;
        }

        return this.getHidingTeam(uuid) != null ? this.getHidingTeam(uuid) : null;
    }

    public AbstractTeam getTeam(Player player) {
        return this.getTeam(player.getUniqueId());
    }

    public HidingTeam getHidingTeam(UUID uuid) {
        for (HidingTeam hidingTeam : this.getHidingTeams()) {
            if (hidingTeam.hasPlayer(uuid)) {
                return hidingTeam;
            }
        }
        return null;
    }

    public void teleportHiders(Location location) {
        for (AbstractTeam team : this.getHidingTeams()) {
            for (Player player : team.getOnlinePlayers()) {
                if (player == null) {
                    log(Level.WARNING, "Tried to teleport non-player entry from the HIDERS team. Skipping...");
                    continue;
                }
                player.teleport(location);
            }
        }
    }

    public void teleportSeekers(Location location) {
        for (Player player : this.getSeekingTeam().getOnlinePlayers()) {
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

    public void resetWorldBorder() {
        this.world.getWorldBorder().reset();
    }

    public void setWorldBorderSize(double size, long seconds) {
        if (this.world.getWorldBorder().getSize() <= 1) {
            return; // TODO: maybe console msg or something saying why it isnt gonan shrink anymore
        }

        this.world.getWorldBorder().setSize(size, seconds);
    }

    public List<Player> getOnlinePlayers() {
        List<Player> playerList = new ArrayList<>(this.getSeekingTeam().getOnlinePlayers());
        for (HidingTeam hidingTeam : this.getHidingTeams()) {
            playerList.addAll(hidingTeam.getOnlinePlayers());
        }

        return playerList;
    }

    public List<HidingTeam> getActiveHidingTeams() {
        return this.getHidingTeams().stream()
                .filter(HidingTeam::isActive)
                .toList();
    }

    public List<UUID> getActiveHiders() {
        List<UUID> untaggedHiders = new ArrayList<>();
        for (HidingTeam hidingTeam : this.getHidingTeams()) {
            untaggedHiders.addAll(hidingTeam.getUuidList().stream()
                    .filter(u -> !hidingTeam.getInactivePlayers().contains(u))
                    .toList());
        }

        return untaggedHiders;
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
