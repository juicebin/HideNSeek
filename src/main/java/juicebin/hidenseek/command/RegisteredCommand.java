package juicebin.hidenseek.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.util.ClassUtils;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
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

    protected abstract void run(CommandSender sender, Command command, String label, String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ClassUtils.getMethodsAnnotatedWith(this.getClass(), SubCommand.class).forEach((method, annotation) -> {
            if (args[0].equals(((SubCommand) annotation).value())) {
                try {
                    method.invoke(this, sender, command, label, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                this.run(sender, command, label, args);
            }
        });

        return true;
    }
}
