package juicebin.hidenseek.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public final class TimeUtils {

    public static String ticksToString(int ticks) {
        long minute = ticks / 1200;
        long second = ticks / 20 - minute * 60;

        return "" + Math.round(minute) + ":" + Math.round(second);
    }

    public static TextComponent ticksToShortTime(int ticks, NamedTextColor numberColor, NamedTextColor textColor) {
        long minute = ticks / 1200;
        long second = ticks / 20 - (minute * 60);

        int roundedMins = Math.round(minute);
        int roundedSecs = Math.round(second);

        TextComponent.Builder time = Component.text();

        if (roundedMins > 0) {
            time.append(Component.text(roundedMins).color(numberColor)).append(Component.text(" minutes").color(textColor));
        } else {
            time.append(Component.text(roundedSecs).color(numberColor)).append(Component.text(" seconds").color(textColor));
        }

        return time.build();
    }

}
