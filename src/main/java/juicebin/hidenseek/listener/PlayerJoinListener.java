package juicebin.hidenseek.listener;

import juicebin.hidenseek.util.ScoreHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

public class PlayerJoinListener extends RegisteredListener {
    private static final TextComponent title = Component.text()
            .append(Component.text("Ikea").color(TextColor.color(235, 192, 0)))
            .append(Component.space())
            .append(Component.text("Hide N Seek").color(TextColor.color(25, 44, 255)))
            .build();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ScoreHelper scoreHelper = ScoreHelper.createScore(event.getPlayer());
        Team team = plugin.getGame().getTeam(event.getPlayer());

        scoreHelper.setTitle(title);
        scoreHelper.setSlot(2, "&fTeam: " + team.getName()); // TODO: team color
    }

}
