package juicebin.hidenseek.util;

import juicebin.hidenseek.HideNSeek;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;

public final class ComponentUtils {

    public static TextComponent createPrefixedComponent(TextComponent component) {
        return HideNSeek.getPrefix().append(Component.space()).style(Style.empty()).append(component);
    }

    public static TextComponent createBulletedComponent(TextComponent component) {
        return createPrefixedComponent(Component.text("  > ").color(NamedTextColor.GRAY).append(component));
    }

    public static TextComponent createCommandComponent(String label, TextColor color, String hoverLabel, String command) {
        return Component.text(label).color(color)
                .hoverEvent(HoverEvent.showText(Component.text(hoverLabel).color(NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand(command));
    }

    public static TextComponent createCommandComponent(TextComponent component, String hoverLabel, String command) {
        return component
                .hoverEvent(HoverEvent.showText(Component.text(hoverLabel).color(NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand(command));
    }

    public static TextComponent createLocationComponent(String label, TextColor color, Location loc) {
        return createCommandComponent(label, color, "Click to teleport", "/tp @s " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
    }

}
