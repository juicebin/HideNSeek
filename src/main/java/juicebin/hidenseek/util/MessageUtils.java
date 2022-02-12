package juicebin.hidenseek.util;

import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class MessageUtils {

    public static void sendMessage(CommandSender sender, MessageLevel level, String string) {
        if (sender instanceof Player) {
            string = level.getColor() + string;
        }

        sender.sendMessage(string);
    }

}
