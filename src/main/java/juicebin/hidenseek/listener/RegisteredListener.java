package juicebin.hidenseek.listener;

import juicebin.hidenseek.HideNSeek;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class RegisteredListener implements Listener {
    protected HideNSeek plugin;

    public void register(final HideNSeek instance) {
        this.plugin = instance;
        Bukkit.getPluginManager().registerEvents(this, instance);
    }
}
