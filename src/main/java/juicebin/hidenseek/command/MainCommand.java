package juicebin.hidenseek.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainCommand extends RegisteredCommand {
    @Override
    protected String getName() {
        return "hideandseek";
    }

    @Override
    protected boolean run(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);

        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "game" -> new GameCommand(plugin).onCommand(sender, command, label, newArgs);
            case "team" -> new TeamCommand(plugin).onCommand(sender, command, label, newArgs);
            default -> false;
        };

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("game", "team");
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase(Locale.ROOT)) {
                case "game" -> {
                    return List.of("start", "stop", "view");
                }
                case "team" -> {
                    return List.of("list", "view-team", "view-player", "view-players");
                }
            }
        }
        return List.of();
    }
}
