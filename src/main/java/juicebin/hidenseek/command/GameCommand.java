package juicebin.hidenseek.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sk89q.worldedit.WorldEdit;
import juicebin.hidenseek.game.Game;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class GameCommand extends RegisteredCommand {
    @Override
    protected String getName() {
        return "game";
    }

    @Override
    protected LiteralArgumentBuilder<?> getArgumentBuilder() {
        return literal(this.getName())
            .then(literal("setup"))
            .then(literal("start"));
    }

    @Override
    protected void run(CommandSender sender, Command command, String label, String[] args) {

    }

    @SubCommand(parent = "create", name = "create")
    public void create(CommandSender sender, Command command, String label, String[] args) {
        // /game create <name> <hider-x> <hider-y> <hider-z> <seeker-x> <seeker-y> <seeker-z>

        if (sender instanceof Player player) {
            int hiderX = Arguments.requireInt(sender, args, 0);
            int hiderY = Arguments.requireInt(sender, args, 1);
            int hiderZ = Arguments.requireInt(sender, args, 2);
            int seekerX = Arguments.requireInt(sender, args, 3);
            int seekerY = Arguments.requireInt(sender, args, 4);
            int seekerZ = Arguments.requireInt(sender, args, 5);

            Location lobbyLocation; // should be a main lobby from the plugin class or smth
            Location hiderLocation = new Location(player.getWorld(), hiderX, hiderY, hiderZ);
            Location seekerLocation = new Location(player.getWorld(), seekerX, seekerY, seekerZ);
            WorldEdit.getInstance().getSessionManager().get(player);
            
        } else {
            sender.sendMessage("You cannot execute this command as a non-player");
        }
    }

    @SubCommand(parent = "game", name = "setup")
    public void start(CommandSender sender, Command command, String label, String[] args) {
        // /game start <name>

        // GameHandler.startGame(new Game(...));
    }
}
