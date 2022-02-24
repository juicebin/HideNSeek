package juicebin.hidenseek.listener;

import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.event.*;
import juicebin.hidenseek.game.Game;
import juicebin.hidenseek.util.MessageUtils;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.Team;

import java.util.Set;
import java.util.logging.Level;

import static juicebin.hidenseek.HideNSeek.log;

public class GameListener extends RegisteredListener {
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

    @EventHandler
    public void onGameStartEvent(GameStartEvent event) {
        log(Level.INFO, "Event called: GameStartEvent");

        Game game = event.getGame();
        World world = game.getWorld();

        Team seekingTeam = game.getSeekingTeam();
        Set<Team> hidingTeams = game.getHidingTeams();

        // Send game start message

        // TP hiders to hider spawn
        game.teleportHiders(game.getHiderSpawn());
    }

    @EventHandler
    public void onGameStopEvent(GameStopEvent event) {
        log(Level.INFO, "Event called: GameStopEvent");

        Game game = event.getGame();
        World world = game.getWorld();

        // Send message

        // TP all players to lobby
        game.teleportSeekers(game.getLobbyLocation());
        game.teleportHiders(game.getLobbyLocation());

        // Send message who wins
        // Change scoreboard to show who wins
    }

    @EventHandler
    public void onHidersGlowEvent(HidersGlowEvent event) {
        log(Level.INFO, "Event called: HidersGlowEvent");

        Game game = event.getGame();
        World world = game.getWorld();
    }

    @EventHandler
    public void seekersReleasedEvent(SeekersReleasedEvent event) {
        log(Level.INFO, "Event called: SeekersReleasedEvent");

        Game game = event.getGame();
        World world = game.getWorld();

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

        game.setWorldBorderSize(plugin.getConfigInstance().getBorderShrinkSize());
    }

    @EventHandler
    public void onSeekerTagHider(SeekerTagHiderEvent event) {
        log(Level.INFO, "Event called: SeekerTagHiderEvent");

        Game game = event.getGame();

        // Turn into spectator mode
        // Give ability to see all hiders client-side
    }

}
