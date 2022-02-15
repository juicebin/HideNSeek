package juicebin.hidenseek.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageUtils {

    public static void sendMessage(CommandSender sender, MessageLevel level, String string) {
        sender.sendMessage(Component.text(string).color(level.getColor()));
    }

    public static void sendMessage(CommandSender sender, TextColor color, String string) {
        sender.sendMessage(Component.text(string).color(color));
    }

    public static void sendMessage(CommandSender sender, Component component) {
        sender.sendMessage(component);
    }

}
