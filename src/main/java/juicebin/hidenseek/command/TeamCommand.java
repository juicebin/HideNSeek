package juicebin.hidenseek.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TeamCommand extends RegisteredCommand {
    @Override
    protected String getName() {
        return "name";
    }

    @Override
    protected void run(CommandSender sender, Command command, String label, String[] args) {

    }

    // /team manage <team> (add/remove) <player>
    // /team create <name>
    // /team remove <name>
}
