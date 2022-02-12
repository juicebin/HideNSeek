package juicebin.hidenseek.util;

public final class NumberUtils {

    public static String convertTicksToTime(int ticks) {
        long minute = ticks / 1200;
        long second = ticks / 20 - (minute * 60);

        return "" + Math.round(minute) + ":" + Math.round(second);
    }

}
