package juicebin.hidenseek.game;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import juicebin.hidenseek.Config;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.*;
import juicebin.hidenseek.util.*;
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
import ru.xezard.glow.data.glow.Glow;

import java.util.*;
import java.util.logging.Level;

import static juicebin.hidenseek.HideNSeek.log;

public final class Game implements Listener {
    public static final ChatColor ALLY_GLOW_COLOR = ChatColor.GREEN;
    public static final ChatColor NEUTRAL_GLOW_COLOR = ChatColor.BLUE;
    public static final ChatColor ENEMY_GLOW_COLOR = ChatColor.RED;
    private final List<UUID> taggedPlayers = new ArrayList<>();
    private final Map<String, HidingTeam> hidingTeamMap = new HashMap<>();
    private final HideNSeek plugin;
    private final World world;
    private final Location lobbyLocation;
    private final Location hiderSpawn;
    private final Location seekerSpawn;
    private final Config config;
    private final int matchTime;
    private SeekingTeam seekingTeam;
    private boolean active;
    private boolean seekersReleased;
    private boolean borderStartedShrink;
    private boolean hidersStartGlow;
    private int ticks;
    public Scoreboard scoreboard;

    public Game(HideNSeek instance, World world, Location lobbyLocation, Location hiderSpawn, Location seekerSpawn) {
        this.plugin = instance;
        this.world = world;
        this.lobbyLocation = lobbyLocation;
        this.hiderSpawn = hiderSpawn;
        this.seekerSpawn = seekerSpawn;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.config = plugin.getConfigInstance();
        this.matchTime = plugin.getConfigInstance().getMatchTime();
    }

    public void updateTeams() {
        FileConfiguration teamConfig = plugin.getTeamConfig();

        // Clear maps
        this.hidingTeamMap.clear();
        this.seekingTeam = null;

        // Initialize hiding teams
        ConfigurationSection hiders = teamConfig.getConfigurationSection("hiders");
        for (String key : hiders.getKeys(false)) {
            ConfigurationSection teamSection = hiders.getConfigurationSection(key);
            String displayName = teamSection.getString("display-name");
            TextColor color = TextColor.color(teamSection.getInt("color"));
            ConfigurationSection players = teamSection.getConfigurationSection("players");
            String prefix = teamSection.getString("prefix");
            String suffix = teamSection.getString("suffix");

            HidingTeam team = new HidingTeam(key, displayName, color, prefix, suffix);
            for (String playerName : players.getKeys(false)) {
                UUID uuid = UUID.fromString(players.getString(playerName));
                team.addPlayer(Bukkit.getOfflinePlayer(uuid));
            }

            hidingTeamMap.put(key, team);
        }

        // Initialize seeking team
        ConfigurationSection seekers = teamConfig.getConfigurationSection("seekers");
        String displayName = seekers.getString("display-name");
        TextColor color = TextColor.color(seekers.getInt("color"));
        ConfigurationSection players = seekers.getConfigurationSection("players");
        String prefix = seekers.getString("prefix");
        String suffix = seekers.getString("suffix");

        SeekingTeam team = new SeekingTeam("seekers", displayName, color, prefix, suffix);
        for (String playerName : players.getKeys(false)) {
            UUID uuid = UUID.fromString(players.getString(playerName));
            team.addPlayer(Bukkit.getOfflinePlayer(uuid));
        }

        this.seekingTeam = team;

        for (HidingTeam hidingTeam : hidingTeamMap.values()) {
            this.scoreboard.registerNewTeam(hidingTeam.getId());
        }

        this.scoreboard.registerNewTeam(seekingTeam.getId());
    }

    public void updateGlow(AbstractTeam team) {
        List<Player> onlinePlayers = team.getOnlinePlayers();
        Glow glow = Glow.builder()
                .plugin(plugin)
                .animatedColor(ALLY_GLOW_COLOR)
                .name("ally")
                .build();
        for (Player player : onlinePlayers) {
            GlowUtils.setGlowing(glow, player, onlinePlayers);
        }
    }

    public void start() {
        ticks = config.getMatchTime();
        active = true;
        seekersReleased = false;
        borderStartedShrink = false;
        hidersStartGlow = false;

        this.initWorldBorder();

        // Set online players to active and their team accordingly
        for (HidingTeam team : this.getHidingTeams()) {
            for (OfflinePlayer player : team.getPlayers()) {
                team.setPlayerActive(player, player.isOnline());
            }
            team.setActive(team.getPlayers().size() >= 1);
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));

        // Hide nameplates for other teams
        for (Team team : this.scoreboard.getTeams()) {
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        }

        // Set seekers can see their teammates glowing
        this.updateGlow(this.getSeekingTeam());

        // Set so hiders can see their teammates glowing
        for (HidingTeam team : this.getActiveHidingTeams()) {
            this.updateGlow(team);
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
        --ticks;

        if (!this.active) return;

        if (ticks % 20 == 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ScoreHelper scoreHelper = ScoreHelper.getByPlayer(player);
                scoreHelper.setSlot(5, "&f╟ &cTime Left: &r" + TimeUtils.ticksToString(ticks));
                scoreHelper.setSlot(4, "&f╟ &cTeams left: &r" + this.getActiveHidingTeams().size());
                scoreHelper.setSlot(3, "&f╟ &cPlayers Left: &r" + this.getActiveHiders().size());
            }
        }

        for (int time : config.getSeekerReleaseWarningTimes()) {
            if (ticks == (config.getMatchTime() - config.getHideTime()) + time) {
                this.sendWarningAlert("Seekers being released in", time);
            }
        }

        if (!seekersReleased && ticks >= (config.getMatchTime() - config.getHideTime()) && ticks % 20 == 0) {
            for (Player player : this.getOnlinePlayers()) {
                player.sendActionBar(Component.text("SEEKERS RELEASE IN: ").color(NamedTextColor.RED)
                        .append(Component.text(TimeUtils.ticksToString((ticks - config.getMatchTime()) + config.getHideTime())).color(NamedTextColor.YELLOW)));
            }
        }

        if (!seekersReleased && ticks <= (config.getMatchTime() - config.getHideTime())) {
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
        MessageUtils.broadcast(Component.text()
                .append(Component.text(text).color(NamedTextColor.RED))
                .append(Component.space())
                .append(TimeUtils.ticksToShortTime(timeLeft, NamedTextColor.YELLOW, NamedTextColor.RED))
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

    public boolean isTagged(OfflinePlayer player) {
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
        return this.seekingTeam.hasPlayer(player);
    }

    public boolean isHider(Player player) {
        for (HidingTeam team : this.getHidingTeams()) {
            if (team.hasPlayer(player)) {
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

    public AbstractTeam getTeam(OfflinePlayer player) {
        if (this.seekingTeam.hasPlayer(player)) {
            return seekingTeam;
        }

        return this.getHidingTeam(player);
    }

    public HidingTeam getHidingTeam(OfflinePlayer player) {
        for (HidingTeam hidingTeam : this.getHidingTeams()) {
            if (hidingTeam.hasPlayer(player)) {
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

    public List<OfflinePlayer> getActiveHiders() {
        List<OfflinePlayer> untaggedHiders = new ArrayList<>();
        for (HidingTeam hidingTeam : this.getHidingTeams()) {
            untaggedHiders.addAll(hidingTeam.getActivePlayers());
        }

        return untaggedHiders;
    }

    public boolean isSeekersReleased() {
        return seekersReleased;
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
