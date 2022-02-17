package juicebin.hidenseek.util;

public final class TickUtils {

    public static String convertTicksToTime(int ticks) {
        long minute = ticks / 1200;
        long second = ticks / 20 - (minute * 60);

        return "" + Math.round(minute) + ":" + Math.round(second);
    }

    public static int convertTicksToMinutes(int ticks) {
        long minute = ticks / 1200;
        return Math.round(minute);
    }

    public static int convertTicksToSeconds(int ticks) {
        long minute = ticks / 1200;
        long second = ticks / 20 - (minute * 60);
        return Math.round(second);
    }

}
