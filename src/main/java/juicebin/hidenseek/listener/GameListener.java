package juicebin.hidenseek.listener;

import juicebin.hidenseek.Config;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.*;
import juicebin.hidenseek.game.Game;
import juicebin.hidenseek.game.HidingTeam;
import juicebin.hidenseek.game.SeekingTeam;
import juicebin.hidenseek.game.AbstractTeam;
import juicebin.hidenseek.util.MessageUtils;
import juicebin.hidenseek.util.ScoreHelper;
import juicebin.hidenseek.util.SoundUtils;
import juicebin.hidenseek.util.TimeUtils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.glow.GlowAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static juicebin.hidenseek.HideNSeek.log;

public final class GameListener extends RegisteredListener {
//    private static final GlowAPI.Color TEAM_MEMBER_COLOR = GlowAPI.Color.GREEN;
//    private static final GlowAPI.Color NEUTRAL_GLOW_COLOR = GlowAPI.Color.BLUE;
//    private static final GlowAPI.Color ENEMY_GLOW_COLOR = GlowAPI.Color.RED;
    private final HideNSeek plugin;

    public GameListener(HideNSeek instance) {
        this.plugin = instance;
    }

    // TODO: Game Process
    //  - [ ] release seekers after x amount of time
    //  - [ ] send info message in chat
    //  - [ ] start the action bar timer
    //  - [ ] make players that are on the team in adventure mode
    //  - [ ] shrinking border after x amount of time
    //  - [ ] start glowing intervals
    //  - [ ] give seekers a trail

    // TODO: Fix player count when player joins/leaves

    @EventHandler
    public void onGameStartEvent(GameStartEvent event) {
        log(Level.INFO, "Event called: GameStartEvent");

        Game game = event.getGame();
        World world = game.getWorld();

        SeekingTeam seekingTeam = game.getSeekingTeam();
        List<HidingTeam> hidingTeams = game.getHidingTeams();

        // Send game start message

        // TP hiders to hider spawn
        game.teleportHiders(game.getHiderSpawn());

        // TODO: Everyone can see their allies
    }

    @EventHandler
    public void onGameStopEvent(GameStopEvent event) {
        log(Level.INFO, "Event called: GameStopEvent");

        Game game = event.getGame();

        new BukkitRunnable() {
            @Override
            public void run() {
                int returnToLobbyDelay = plugin.getConfigInstance().getReturnToLobbyDelay();
                MessageUtils.broadcastSubtitle(Component.text("Returning to lobby in ")
                        .color(NamedTextColor.RED)
                        .append(TimeUtils.ticksToShortTime(returnToLobbyDelay, NamedTextColor.GOLD, NamedTextColor.RED)));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // TP all players to lobby and give all players gamemode survival
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.teleport(game.getLobbyLocation());
                            player.setGameMode(GameMode.SURVIVAL);
                        }
                    }
                }.runTaskLater(plugin, returnToLobbyDelay);
            }
        }.runTaskLater(plugin, 100);

        // TODO: Remove all glows

        if (!event.isForced()) {
            List<AbstractTeam> winningTeams = new ArrayList<>();

            // Decides the winning teams
            List<HidingTeam> activeHidingTeams = game.getActiveHidingTeams();
            if (activeHidingTeams.size() > 1) {
                // The remaining hider teams win
                winningTeams.addAll(activeHidingTeams);
            } else {
                winningTeams.add(game.getSeekingTeam());
            }

            // Create winning message
            TextComponent.Builder builder = Component.text();
            NamedTextColor defaultColor = NamedTextColor.WHITE;
            int size = winningTeams.size();

            if (size == 1) {
                AbstractTeam team = winningTeams.get(0);
                builder.append(Component.text("wins!").color(defaultColor));
            } else if (size == 2) {
                AbstractTeam team1 = winningTeams.get(0);
                AbstractTeam team2 = winningTeams.get(1);

                builder.append(team1.getDisplayName())
                        .append(Component.space())
                        .append(Component.text("and").color(defaultColor))
                        .append(Component.space())
                        .append(team2.getDisplayName());

                builder.append(Component.text("win!").color(defaultColor));
            } else {
                for (int i = 0; i < winningTeams.size(); i++) {
                    AbstractTeam team = winningTeams.get(i);

                    builder.append(team.getDisplayName());

                    if (i == winningTeams.size() - 2) {
                        builder.append(Component.text(" and ").color(defaultColor));
                    } else if (i != winningTeams.size() - 1) {
                        builder.append(Component.text(", ").color(defaultColor));
                    }
                }
                builder.append(Component.text("win!").color(defaultColor));
            }

            TextComponent winningMessage = builder.build();

            // Send message who wins
            // TODO: Also send that the game has ended
            MessageUtils.broadcastSubtitle(winningMessage);
            SoundUtils.broadcastSound(Sound.sound(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, Sound.Source.MASTER, 0.5f, 1.0f));

            // Change scoreboard to show who wins
            for (Player player : event.getGame().getOnlinePlayers()) {
                ScoreHelper scoreHelper = ScoreHelper.getByPlayer(player);
                // TODO: Change scoreboard to show who won

                // 8     ╔══════════════════
                // 7     ╟ Winning Team(s):
                // 6     ╟ - Team 1
                // 5     ╟ - Team 2
                // 4     ╟ - Team 3
                // 3     ╟ - Team 4
                // 2     ╚══════════════════
                // 1      @DotWavPresents

                scoreHelper.setSlot(2, "╚══════════════════");
                scoreHelper.setSlot(1, "&7@DotWavPresents");

                int slot = 3;

                for (AbstractTeam team : winningTeams) {
                    scoreHelper.setSlot(slot, Component.text("╟ - ").color(NamedTextColor.WHITE).append(team.getDisplayName()));
                    slot++;
                }

                scoreHelper.setSlot(slot, "╟ &eWinning Team(s)&f:");
                scoreHelper.setSlot(slot + 1, "╔══════════════════");
            }
        } else {
            // TODO: Send that the game was forcefully stopped
        }
    }

    @EventHandler
    public void onHidersGlowEvent(HidersGlowEvent event) {
        log(Level.INFO, "Event called: HidersGlowEvent");

        Game game = event.getGame();
        World world = game.getWorld();

        // TODO: HidersGlowEvent
    }

    @EventHandler
    public void seekersReleasedEvent(SeekersReleasedEvent event) {
        log(Level.INFO, "Event called: SeekersReleasedEvent");

        Game game = event.getGame();
        World world = game.getWorld();

        // TODO: Seeker release message

        // TP seekers to seeker spawn
        game.teleportSeekers(game.getSeekerSpawn());
    }

    @EventHandler
    public void onBorderShrinkEvent(BorderShrinkEvent event) {
        log(Level.INFO, "Event called: BorderShrinkEvent");

        Game game = event.getGame();

        // Send warning message
        if (event.isFirstEvent()) {
            MessageUtils.broadcastSubtitle(Component.text("THE BORDER IS STARTING TO SHRINK").color(NamedTextColor.RED));
        } else {
            MessageUtils.broadcastActionbar(Component.text("The border is shrinking!").color(NamedTextColor.RED));
        }

        // Decrease the world border size from A to B over X amount of time
        Config config = plugin.getConfigInstance();
        game.setWorldBorderSize(game.getWorld().getWorldBorder().getSize() - config.getBorderShrinkSize(), config.getBorderShrinkTime());
    }

    @EventHandler
    public void onSeekerTagHider(SeekerTagHiderEvent event) {
        log(Level.INFO, "Event called: SeekerTagHiderEvent");

        Player hider = event.getHider();
        Player seeker = event.getSeeker();
        Game game = event.getGame();

        // Send tag message
        MessageUtils.broadcast(Component.text()
                .append(Component.text(seeker.getName()).color(game.getTeam(seeker).getColor()))
                .append(Component.space())
                .append(Component.text("tagged").color(NamedTextColor.YELLOW))
                .append(Component.space())
                .append(Component.text(hider.getName()).color(game.getTeam(hider).getColor()))
                .build());

        // Play firework particle

        // Play sound for both players
        hider.getWorld().playSound(hider.getLocation(), org.bukkit.Sound.ENTITY_ARROW_HIT_PLAYER, 0.5f, 1.0f);

        // Turn into spectator mode
        event.getHider().setGameMode(GameMode.SPECTATOR);

        // Give ability to see all hiders and seekers client-side
        // TODO: Glow effect

        HidingTeam hidingTeam = game.getHidingTeam(hider.getUniqueId());

        if (hidingTeam != null) {
            // Eliminate team if they were the last one
            List<Player> onlinePlayers = hidingTeam.getOnlinePlayers();
            if (onlinePlayers != null && onlinePlayers.size() <= 1) {
                hidingTeam.setActive(false);

                MessageUtils.broadcast(hidingTeam.getDisplayName().append(Component.text(" has been eliminated!").color(NamedTextColor.RED)));
                SoundUtils.broadcastSound(Sound.sound(org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, Sound.Source.MASTER, 1.0f, 1.0f));

                for (OfflinePlayer player : hidingTeam.getPlayers()) {
                    if (player.isOnline()) {
                        MessageUtils.broadcastSubtitle(Component.text("Your team has been eliminated!").color(NamedTextColor.RED));
                    }
                }
            }

            // Set player to inactive
            hidingTeam.setPlayerActive(hider, false);
        }
    }

}
