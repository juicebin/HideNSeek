package juicebin.hidenseek.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.game.Game;
import juicebin.hidenseek.game.GameHandler;
import juicebin.hidenseek.util.MessageLevel;
import juicebin.hidenseek.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class GameCommand extends RegisteredCommand {
    @Override
    protected String getName() {
        return "game";
    }

    @Override
    protected void run(CommandSender sender, Command command, String label, String[] args) {
        GameHandler gameHandler = plugin.getGameHandler();

        if (args[0].equalsIgnoreCase("create")) {
            // /game create <name> <hider-x> <hider-y> <hider-z> <seeker-x> <seeker-y> <seeker-z>
            if (sender instanceof Player player) {
                String name = args[1];
                double hiderX = Double.parseDouble(args[2]);
                double hiderY = Double.parseDouble(args[3]);
                double hiderZ = Double.parseDouble(args[4]);
                double seekerX = Double.parseDouble(args[5]);
                double seekerY = Double.parseDouble(args[6]);
                double seekerZ = Double.parseDouble(args[7]);

                Location hiderLocation = new Location(player.getWorld(), hiderX, hiderY, hiderZ);
                Location seekerLocation = new Location(player.getWorld(), seekerX, seekerY, seekerZ);

                gameHandler.registerGame(new Game(this.plugin, name, player.getWorld(), this.plugin.getConfigInstance().getLobbyLocation(), hiderLocation, seekerLocation));
                MessageUtils.sendMessage(player, MessageLevel.SUCCESS, "Game \"" + name + "\" successfully registered");
            } else {
                MessageUtils.sendMessage(sender, MessageLevel.ERROR, "You cannot execute this command as a non-player");
            }
        } else if (args[0].equalsIgnoreCase("view")) {
            Game game = gameHandler.getGame(args[1]);
            if (game == null) {
                MessageUtils.sendMessage(sender, MessageLevel.ERROR, "There is no game with that specified ID.");
                return;
            }
            MessageUtils.sendMessage(sender, this.getGameInfo(game));
        } else if (args[0].equalsIgnoreCase("list")) {
            TextComponent.Builder builder = Component
                    .text()
                    .color(TextColor.color(255, 246, 103));

            for (Game game : gameHandler.getGames()) {
                builder
                        .append(Component.text("1. ").color(NamedTextColor.WHITE))
                        .append(Component.text("\"" + game.getId() + "\"").color(NamedTextColor.YELLOW)
                                .hoverEvent(HoverEvent.showText(Component.text("Click to view info").color(NamedTextColor.AQUA)))
                                .clickEvent(ClickEvent.runCommand("/game view " + game.getId())))
                        .append(Component.text(" - ").color(NamedTextColor.WHITE))
                        .append(Component.text(game.isActive() ? "ACTIVE" : "INACTIVE").color(game.isActive() ? NamedTextColor.GREEN : NamedTextColor.RED));
            }

            if (gameHandler.getGames().size() > 0) {
                MessageUtils.sendMessage(sender, builder.build());
            } else {
                MessageUtils.sendMessage(sender, MessageLevel.ERROR, "There are no registered games.");
            }
        } else {
            Game game = plugin.getGameHandler().getGame(args[1]);
            if (game == null) {
                MessageUtils.sendMessage(sender, MessageLevel.ERROR, "There is no game with that specified ID.");
                return;
            }
            if (args[0].equalsIgnoreCase("start")) {
                if (game.isActive()) {
                    MessageUtils.sendMessage(sender, MessageLevel.ERROR, "Game is already started.");
                    return;
                }

                game.start();
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (!game.isActive()) {
                    MessageUtils.sendMessage(sender, MessageLevel.ERROR, "Game is already stopped.");
                    return;
                }

                game.stop();
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return List.of();

        Location location = player.getLocation();

        if (args[0].equalsIgnoreCase("create")) {
            return switch (args.length) {
                case 2 -> List.of("name");
                case 3, 6 -> List.of(String.valueOf(location.getX()));
                case 4, 7 -> List.of(String.valueOf(location.getY()));
                case 5, 8 -> List.of(String.valueOf(location.getZ()));
                default -> List.of();
            };
        } else if (args[0].equalsIgnoreCase("start")) {

        } else if (args[0].equalsIgnoreCase("stop")) {

        } else if (args[0].equalsIgnoreCase("view")) {

        } else if (args[0].equalsIgnoreCase("list")) {

        }

        return List.of("create", "start", "stop", "view", "list");
    }

    @Override
    public LiteralCommandNode<?> getLiteralCommandNode() {
        DoubleArgumentType xPosArg = DoubleArgumentType.doubleArg();
        DoubleArgumentType yPosArg = DoubleArgumentType.doubleArg();
        DoubleArgumentType zPosArg = DoubleArgumentType.doubleArg();

        xPosArg.getExamples().add("");

        LiteralArgumentBuilder<?> argumentBuilder = literal("game")
                .then(literal("create")
                        .then(argument("name", StringArgumentType.word())
                        .then(argument("hider-x", xPosArg)
                        .then(argument("hider-y", yPosArg)
                        .then(argument("hider-z", yPosArg)
                        .then(argument("seeker-z", zPosArg)
                        .then(argument("seeker-x", xPosArg)
                        .then(argument("seeker-y", yPosArg)
                        .then(argument("seeker-z", zPosArg)
                        ))))))))
                ).then(literal("start")
                        .then(argument("name", StringArgumentType.word()))
                ).then(literal("stop")
                        .then(argument("name", StringArgumentType.word()))
                ).then(literal("view")
                        .then(argument("name", StringArgumentType.word()))
                ).then(literal("list"));

        return argumentBuilder.build();
    }

    private TextComponent getGameInfo(Game game) {
        return Component.text()
                .append(this.createPrefixedComponent(
                        Component.text()
                                .append(Component.text("Game: ").color(NamedTextColor.WHITE))
                                .append(Component.space())
                                .append(Component.text("\"" + game.getId() + "\"")).color(NamedTextColor.YELLOW)
                                .build()))
                .append(Component.newline())
                .append(this.createBulletedComponent(this.createLocationComponent("Hider Spawn", NamedTextColor.AQUA, game.getHiderSpawn())))
                .append(Component.newline())
                .append(this.createBulletedComponent(this.createLocationComponent("Seeker Spawn", NamedTextColor.AQUA, game.getSeekerSpawn())))
                .append(Component.newline())
                .append(this.createBulletedComponent(this.createGameControlComponent(game)))
                .build();
    }

    private @NotNull TextComponent createLocationComponent(String label, TextColor color, Location loc) {
        return Component.text(label).color(color)
                .hoverEvent(HoverEvent.showText(Component.text("Click to teleport").color(NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand("/tp @s " + loc.getX() + " " + loc.getY() + " " + loc.getZ()));
    }

    private TextComponent createPrefixedComponent(TextComponent component) {
        return HideNSeek.getPrefix().append(Component.space()).append(component);
    }

    private TextComponent createBulletedComponent(TextComponent component) {
        return this.createPrefixedComponent(Component.text("> ").color(NamedTextColor.GRAY).append(component));
    }

    private TextComponent createGameControlComponent(Game game) {
        TextComponent stopComponent = Component.text("Stop Game")
                .color(NamedTextColor.RED)
                .hoverEvent(HoverEvent.showText(Component.text("Click to stop").color(NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand("/game stop " + game.getId()));
        TextComponent startComponent = Component.text("Start Game")
                .color(NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text("Click to start").color(NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand("/game start " + game.getId()));
        return game.isActive() ? stopComponent : startComponent;
    }
}
