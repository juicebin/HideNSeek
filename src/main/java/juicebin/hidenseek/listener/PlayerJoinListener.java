package juicebin.hidenseek.listener;

import juicebin.hidenseek.game.Game;
import juicebin.hidenseek.util.ScoreHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

public class PlayerJoinListener extends RegisteredListener {
    private static final TextComponent title = Component.text()
            .append(Component.text("Ikea").color(TextColor.color(235, 192, 0)))
            .append(Component.space())
            .append(Component.text("Hide N Seek").color(TextColor.color(25, 44, 255)))
            .build();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ScoreHelper scoreHelper = ScoreHelper.createScore(player);
        Game game = plugin.getGame();
        Team team = game.getTeam(player);

        scoreHelper.setTitle(title);
        scoreHelper.setSlot(2, "&fTeam: " + team.getName()); // TODO: team color

        // If the game is active and the player was on a team, add them back to the active player list
        if (game.isActive() && game.getTeam(player) != null) {
            game.addActivePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGame();

        // If the game is active and the player was on a team, remove them from the active player list
        if (game.isActive() && game.getTeam(player) != null) {
            game.removeActivePlayer(player);
        }
    }

}
