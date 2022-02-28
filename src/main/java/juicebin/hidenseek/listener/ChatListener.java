package juicebin.hidenseek.listener;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import juicebin.hidenseek.game.Game;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ChatListener extends RegisteredListener {

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGame();

        TextColor color;

        if (game.getTeam(player) != null) {
            color = game.getTeam(player).getColor();
        } else {
            color = NamedTextColor.WHITE;
        }

        HSVLike hsv = color.asHSV();
        TextColor messageColor = TextColor.color(HSVLike.of(hsv.h(), hsv.s() / 4.0f, hsv.v()));

        // TODO: Make configurable

        event.renderer().render(player,
                Component.text(player.getName()).color(color),
                event.originalMessage().color(TextColor.color(messageColor)),
                Audience.audience(event.viewers()));

//        event.message(Component.text(player.getName()).color(color)
//                .append(Component.text(":").color(NamedTextColor.WHITE))
//                .append(Component.space())
//                .append(event.originalMessage()).color(TextColor.color(messageColor)));
    }

}
