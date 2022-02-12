package juicebin.hidenseek.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import juicebin.hidenseek.game.Game;
import juicebin.hidenseek.game.GameHandler;
import juicebin.hidenseek.util.MessageLevel;
import juicebin.hidenseek.util.MessageUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class GameCommand extends RegisteredCommand {
    @Override
    protected String getName() {
        return "game";
    }

    @Override
    protected void run(CommandSender sender, Command command, String label, String[] args) {

    }

    @SubCommand(parent = "create", name = "create")
    public void create(CommandSender sender, Command command, String label, String[] args) {
        // /game create <name> <hider-x> <hider-y> <hider-z> <seeker-x> <seeker-y> <seeker-z>

        if (sender instanceof Player player) {
            String name = args[0];
            int hiderX = Arguments.requireInt(player, args, 1);
            int hiderY = Arguments.requireInt(player, args, 2);
            int hiderZ = Arguments.requireInt(player, args, 3);
            int seekerX = Arguments.requireInt(player, args, 4);
            int seekerY = Arguments.requireInt(player, args, 5);
            int seekerZ = Arguments.requireInt(player, args, 6);

            Location hiderLocation = new Location(player.getWorld(), hiderX, hiderY, hiderZ);
            Location seekerLocation = new Location(player.getWorld(), seekerX, seekerY, seekerZ);

            GameHandler.registerGame(new Game(this.plugin, name, this.plugin.getConfigInstance().getLobbyLocation(), hiderLocation, seekerLocation));
            MessageUtils.sendMessage(player, MessageLevel.SUCCESS, "Game \"" + name + "\" successfully registered");
        } else {
            MessageUtils.sendMessage(sender, MessageLevel.ERROR, "You cannot execute this command as a non-player");
        }
    }

    @SubCommand(parent = "game", name = "setup")
    public void start(CommandSender sender, Command command, String label, String[] args) {
        // /game start <name>

        // GameHandler.startGame(new Game(...));
    }
}
