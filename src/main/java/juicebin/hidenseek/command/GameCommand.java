package juicebin.hidenseek.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import juicebin.hidenseek.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (args[0]) {
            case "setup": this.setup(newArgs);
            case "start": this.start(newArgs);
        }

        return false;
    }

    public void setup(String[] args) {

    }

    public void start(String[] args) {
        // GameHandler.startGame(new Game(...));
    }
}
