package juicebin.hidenseek.listener;

import juicebin.hidenseek.game.Game;
import juicebin.hidenseek.game.HidingTeam;
import juicebin.hidenseek.game.AbstractTeam;
import juicebin.hidenseek.util.ScoreHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener extends RegisteredListener {
    private static final TextComponent TITLE = Component.text()
            .append(Component.text("Ikea").color(TextColor.color(235, 192, 0)))
            .append(Component.space())
            .append(Component.text("Hide N Seek").color(TextColor.color(25, 44, 255)))
            .build();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ScoreHelper scoreHelper = ScoreHelper.createScore(player);
        Game game = plugin.getGame();
        AbstractTeam team = game.getTeam(player.getUniqueId());

        scoreHelper.setTitle(TITLE);

        Component displayName = team == null ? Component.text("N/A").color(NamedTextColor.GRAY) : team.getDisplayName();
        scoreHelper.setSlot(2, "&fTeam: " + displayName); // TODO: team color

        // If the game is active and the player was on a team, set them to untagged
        if (game.isActive() && team instanceof HidingTeam) {
            ((HidingTeam) team).setPlayerActive(player, false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGame();
        AbstractTeam team = game.getTeam(player.getUniqueId());

        // If the game is active and the player was on a team, remove them from the active player list
        if (game.isActive() && team instanceof HidingTeam) {
            ((HidingTeam) team).setPlayerActive(player, false);
        }
    }

}