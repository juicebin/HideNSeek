package juicebin.hidenseek.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class RegisteredListener implements Listener {
    public void register(final Plugin instance) {
        Bukkit.getPluginManager().registerEvents(this, instance);
    }
}
