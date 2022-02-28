package juicebin.hidenseek.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ru.xezard.glow.data.glow.Glow;

import java.util.Collection;

public final class GlowUtils {

    public static void setGlowing(Glow glow, Player viewer, Collection<? extends Entity> holders) {
        holders.forEach(glow::addHolders);
        glow.display(viewer);
    }

    public static void removeGlowing(Glow glow, Player viewer, Collection<? extends Entity> holders) {
        glow.hideFrom(viewer);
        holders.forEach(glow::removeHolders);
        glow.destroy();
    }

}
