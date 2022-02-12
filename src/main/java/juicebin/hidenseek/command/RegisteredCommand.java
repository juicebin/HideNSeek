package juicebin.hidenseek.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.util.ClassUtils;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class RegisteredCommand implements CommandExecutor {
    protected HideNSeek plugin;

    public void register(HideNSeek instance) {
        this.plugin = instance;
        Optional.ofNullable(instance.getCommand(this.getName())).ifPresentOrElse(pluginCommand -> {
            pluginCommand.setExecutor(this);

            if (CommodoreProvider.isSupported()) {
                try {
                    LiteralCommandNode<?> commandNode = CommodoreFileFormat.parse(plugin.getResource(this.getName() + ".commodore"));
                    instance.getCommodore().register(pluginCommand, commandNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, () -> {
            // TODO: Exception handling
        });
    }

    protected abstract String getName();

    protected abstract void run(CommandSender sender, Command command, String label, String[] args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!this.checkSubcommands(sender, command, label, args)) {
            this.run(sender, command, label, args);
        }

        return true;
    }

    private boolean checkSubcommands(CommandSender sender, Command command, String label, String[] args) {
        List<Method> methodList = ClassUtils.getMethodsAnnotatedWith(this.getClass(), SubCommand.class)
                .stream()
                .filter(method -> {
                    SubCommand subCommand = method.getAnnotation(SubCommand.class);

                    if (command.getName().equals(subCommand.parent()) && args[0].equals(subCommand.name())) {
                        // If the command name is equal to the subcommand's parent's name
                        // and the first argument is equal to the name of the subcommand
                        // e.g. /test target-cmd
                        // The name of the command, test, is equal to the subcommand's parent's name, test
                        // The first argument, target-cmd, is equal to the name of the subcommand, target-cmd


                    } else if (args[0].equals(subCommand.parent())) {
                        // If the first argument is equal to the subcommand's parent's name
                        // e.g. /test cmd target-cmd
                        // The first argument, cmd, is equal to target-cmd's parent's name, cmd
                    }
                    
                    if (command.getName().equals(subCommand.parent()) && args[0].equals(subCommand.name())) {
                        try {
                            this.checkSubcommands(sender, command, label, Arrays.copyOfRange(args, 1, args.length + 1));
                            method.invoke(this, sender, command, label, args);
                            return true;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }).collect(Collectors.toList());

        return methodList.size() > 0;
    }
}
