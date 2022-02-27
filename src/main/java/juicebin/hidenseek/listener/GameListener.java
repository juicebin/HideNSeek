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

            int size = winningTeams.size();

            if (size == 1) {
                AbstractTeam winningTeam = winningTeams.get(0);
                builder.append(winningTeam.getDisplayName());
            } else {
                for (int i = 0; i < size; i++) {
                    AbstractTeam winningTeam = winningTeams.get(i);

                    builder.append(winningTeam.getDisplayName());

                    // INDEX  : 0, 1, 2, 3 and 4
                    // LENGTH : 1  2  3  4     5

                    if (i == 1) {
                        // Team 1 WINS!

                    } else if (i == 2) {
                        // Team 2 and Team 3 WIN!
                        builder.append();
                    } else if (i >= 3) {
                        // Team 2, Team 3, and Team 4 WIN!
                    }

                    if (i == size) {
                        builder.append(Component.text(" and ").color(NamedTextColor.WHITE));
                    } else {
                        builder.append(Component.text(", ").color(NamedTextColor.WHITE));
                    }
                }
            }

            if (size == 1) {

            } else if (size == 2) {

            } else {

            }

            TextComponent winningMessage = builder.build()
                    .append(Component.text("WINS!").color(NamedTextColor.WHITE));

            // Send message who wins
            // TODO: Also send that the game has ended
            MessageUtils.broadcastSubtitle(winningMessage);
            SoundUtils.broadcastSound(Sound.sound(org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, Sound.Source.MASTER, 0.5f, 1.0f));

            // Change scoreboard to show who wins
            for (Player player : event.getGame().getOnlinePlayers()) {
                ScoreHelper scoreHelper = ScoreHelper.getByPlayer(player);
                // TODO: Change scoreboard to show who won
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

        if (event.isFirstEvent()) {
            MessageUtils.sendWarningTitle("THE BORDER IS STARTING TO SHRINK");
        }

        // TODO: Send warning message

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

        // Turn into spectator mode
        event.getHider().setGameMode(GameMode.SPECTATOR);

        // Give ability to see all hiders and seekers client-side


        HidingTeam hidingTeam = game.getHidingTeam(hider.getUniqueId());

        if (hidingTeam != null) {
            // Eliminate team if they were the last one
            List<Player> onlinePlayers = hidingTeam.getOnlinePlayers();
            if (onlinePlayers != null && onlinePlayers.size() <= 1) {
                hidingTeam.setActive(false);
                // TODO: Send elimination message
            }

            // Set player to inactive
            hidingTeam.setPlayerActive(hider, false);
        }
    }

}
