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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static juicebin.hidenseek.HideNSeek.log;

public final class GameListener extends RegisteredListener {
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
    }

    @EventHandler
    public void onGameStopEvent(GameStopEvent event) {
        log(Level.INFO, "Event called: GameStopEvent");

        Game game = event.getGame();

        // TP all players to lobby and give all players gamemode survival
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(game.getLobbyLocation());
            player.setGameMode(GameMode.SURVIVAL);
        }

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

            if (winningTeams.size() == 1) {
                AbstractTeam winningTeam = winningTeams.get(0);
                builder.append(winningTeam.getDisplayName());
            } else {
                for (int i = 0; i < winningTeams.size(); i++) {
                    AbstractTeam winningTeam = winningTeams.get(i);

                    builder.append(winningTeam.getDisplayName());

                    // INDEX  : 0, 1, 2, 3 and 4
                    // LENGTH : 1  2  3  4     5

                    if (i == winningTeams.size() - 1) {
                        builder.append(Component.text(" and ").color(NamedTextColor.WHITE));
                    } else if (i != winningTeams.size() - 2) {
                        builder.append(Component.text(", ").color(NamedTextColor.WHITE));
                    }
                }
            }

            TextComponent winningMessage = builder.build()
                    .append(Component.space())
                    .append(Component.text("WINS!").color(NamedTextColor.WHITE));

            // Send message who wins
            // TODO: Also send that the game has ended
            MessageUtils.broadcast(winningMessage);

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
        game.setWorldBorderSize(config.getBorderShrinkSize(), config.getBorderShrinkTime());
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
