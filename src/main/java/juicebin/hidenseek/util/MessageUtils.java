package juicebin.hidenseek.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;

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

    public static void broadcast(Component component) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(component);
        }
    }

    public static void broadcast(String text) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
        }
    }

    public static void broadcastTitle(TextComponent title) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(title, Component.text().build()));
        }
    }

    public static void broadcastSubtitle(TextComponent title) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(Component.text().build(), title));
        }
    }

    public static void broadcastActionbar(TextComponent actionbar) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(actionbar);
        }
    }

}
