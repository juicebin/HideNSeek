package juicebin.hidenseek.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

public enum MessageLevel {
    ERROR(NamedTextColor.RED),
    SUCCESS(NamedTextColor.GREEN);

    private final TextColor color;

    MessageLevel(TextColor color) {
        this.color = color;
    }

    public @Nullable TextColor getColor() {
        return this.color;
    }
}
