package juicebin.hidenseek.event;

import org.bukkit.plugin.Plugin;

import java.util.List;

public final class Listeners {
    private final List<RegisteredListener> listenerList;

    public Listeners(RegisteredListener... listeners) {
        this.listenerList = List.of(listeners);
    }

    public void register(Plugin instance) {
        for (RegisteredListener listener : listenerList) {
            listener.register(instance);
        }
    }

}
