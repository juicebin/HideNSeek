package juicebin.hidenseek.util;

import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SoundUtils {

    public static void broadcastSound(Sound sound) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(sound);
        }
    }

}
