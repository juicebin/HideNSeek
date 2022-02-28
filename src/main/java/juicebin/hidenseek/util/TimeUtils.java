package juicebin.hidenseek.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public final class TimeUtils {

    public static String ticksToString(int ticks) {
        long minute = ticks / 1200;
        long second = ticks / 20 - (minute * 60);

        int roundMinutes = Math.round(minute);
        int roundSeconds = Math.round(second);

        String minString = String.valueOf(roundMinutes);
        String secString = String.valueOf(roundSeconds);

//        minString = minString.toCharArray().length == 1 ? "0" + minString : minString;
        secString = secString.toCharArray().length == 1 ? "0" + secString : secString;

        return "" + minString + ":" + secString;
    }

    public static TextComponent ticksToShortTime(int ticks, NamedTextColor numberColor, NamedTextColor textColor) {
        long minute = ticks / 1200;
        long second = ticks / 20 - (minute * 60);

        int roundedMins = Math.round(minute);
        int roundedSecs = Math.round(second);

        TextComponent.Builder time = Component.text();

        if (roundedMins > 0) {
            if (roundedSecs > 0) {
                time.append(Component.text(roundedMins).color(numberColor))
                        .append(Component.text(" minutes and ").color(textColor))
                        .append(Component.text(roundedSecs).color(numberColor))
                        .append(Component.text(" seconds").color(textColor));
            } else {
                time.append(Component.text(roundedMins).color(numberColor)).append(Component.text(" minutes").color(textColor));
            }
        } else {
            time.append(Component.text(roundedSecs).color(numberColor)).append(Component.text(" seconds").color(textColor));
        }

        return time.build();
    }

}
