package juicebin.hidenseek.util;

import org.bukkit.Color;

public enum MessageLevel {
    ERROR(Color.RED),
    SUCCESS(Color.GREEN);

    private final Color color;

    MessageLevel(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }
}
