package juicebin.hidenseek.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import juicebin.hidenseek.HideNSeek;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public abstract class RegisteredCommand implements CommandExecutor {
    public void register(HideNSeek instance) {
        Optional.ofNullable(instance.getCommand(this.getName())).ifPresentOrElse(pluginCommand -> {
            pluginCommand.setExecutor(this);

            if (CommodoreProvider.isSupported()) {
                instance.getCommodore().register(pluginCommand, this.getArgumentBuilder());
            }
        }, () -> {
            // TODO: Exception handling
        });
    }

    protected abstract String getName();

    protected abstract LiteralArgumentBuilder<?> getArgumentBuilder();
}
