package juicebin.hidenseek.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.game.Game;
import juicebin.hidenseek.util.MessageLevel;
import juicebin.hidenseek.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class GameCommand extends RegisteredCommand {
    @Override
    protected String getName() {
        return "game";
    }

    @Override
    protected boolean run(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            MessageUtils.sendMessage(sender, MessageLevel.ERROR, "You cannot execute this command as a non-player");
            return true;
        }

        Game game = plugin.getGame();

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "view" -> {
                if (args.length != 1) return false;

                MessageUtils.sendMessage(player, this.getGameInfo(game));
            }
            case "start", "stop" -> {
                if (args.length != 1) return false;

                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "start" -> {
                        // TODO: Countdown

                        if (game.isActive()) {
                            MessageUtils.sendMessage(player, MessageLevel.ERROR, "Game is already started.");
                            return true;
                        }

                        MessageUtils.sendMessage(player, MessageLevel.SUCCESS, "Starting game...");
                        game.start();
                    }
                    case "stop" -> {
                        if (!game.isActive()) {
                            MessageUtils.sendMessage(player, MessageLevel.ERROR, "Game is already stopped.");
                            return true;
                        }

                        MessageUtils.sendMessage(player, MessageLevel.SUCCESS, "Stopping game...");
                        game.stop();
                    }
                }
            }
            case "manage" -> {
                switch (args[1].toLowerCase(Locale.ROOT)) {
                    case "teams" -> {
                        // /game manage <name> teams manage <team> add-player <player>
                        switch (args[2].toLowerCase(Locale.ROOT)) {
                            case "manage" -> {
                                Team team = game.getTeam(args[3]);
                                if (team == null) {
                                    MessageUtils.sendMessage(player, MessageLevel.ERROR, "There is no team with that specified ID.");
                                    return true;
                                }

                                switch (args[4].toLowerCase(Locale.ROOT)) {
                                    case "add-player", "remove-player" -> {
                                        Player targetPlayer = Bukkit.getPlayer(args[5]);

                                        if (targetPlayer == null) {
                                            MessageUtils.sendMessage(player, MessageLevel.ERROR, "There is no player online with that name.");
                                            return true;
                                        }

                                        String msg = "";

                                        switch (args[4].toLowerCase(Locale.ROOT)) {
                                            case "add-player" -> {
                                                team.addPlayer(targetPlayer);
                                                msg = String.format("Player '%s' successfully added to team '%s'.", player.getName(), team.getName());
                                            }
                                            case "remove-player" -> {
                                                team.removePlayer(targetPlayer);
                                                msg = String.format("Player '%s' successfully removed from team '%s'.", player.getName(), team.getName());
                                            }
                                        }

                                        MessageUtils.sendMessage(player, MessageLevel.SUCCESS, msg);
                                        MessageUtils.sendMessage(player, MessageLevel.ERROR, "WARNING: This is an instance-based outcome. The teams will reset when the server or plugin is reloaded.");
                                    }
                                    case "info" -> MessageUtils.sendMessage(player, this.getTeamInfo(game, team));
                                }
                            }
                            case "list" -> {
                                if (game.getTeams().size() <= 0) {
                                    MessageUtils.sendMessage(player, MessageLevel.ERROR, "There are no players on this team.");
                                    return true;
                                }

                                MessageUtils.sendMessage(player, this.getTeamListInfo(game));
                            }
                        }
                    }
                    case "display-player" -> {
                        Player targetPlayer = Bukkit.getPlayer(args[3]);

                        if (targetPlayer == null) {
                            MessageUtils.sendMessage(player, MessageLevel.ERROR, "There is no player online with that name.");
                            return true;
                        }

                        MessageUtils.sendMessage(player, this.getPlayerInfo(game, player));
                    }
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return List.of();

        Location location = player.getLocation();

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "create" -> {
                return switch (args.length) {
                    case 2 -> List.of("<name>");
                    case 3, 6 -> List.of(String.valueOf(location.getBlockX()));
                    case 4, 7 -> List.of(String.valueOf(location.getBlockY()));
                    case 5, 8 -> List.of(String.valueOf(location.getBlockZ()));
                    default -> List.of();
                };
            }
            case "start", "stop", "view" -> {
                return List.of();
            }
            case "manage" -> {
                if (args.length == 2) {
                    return List.of("teams", "display-player");
                } else if (args.length == 3) {
                    if (args[1].equalsIgnoreCase("teams")) {
                        // /game manage teams
                        return List.of("manage");
                    } else {
                        return List.of();
                    }
                } else if (args.length == 4) {
                    if (args[1].equalsIgnoreCase("teams") && args[2].equalsIgnoreCase("manage")) {
                        // /game manage teams manage
                        return List.of("add-player", "remove-player", "info");
                    } else {
                        return List.of();
                    }
                } else {
                    return List.of();
                }
            }
            default -> {
                if (args.length == 1)  {
                    return List.of("create", "start", "stop", "view", "list");
                } else {
                    return List.of();
                }
            }
        }
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
                                .append(Component.text("Game Info:").color(NamedTextColor.WHITE))
                                .build()))
                .append(Component.newline())
                .append(this.createBulletedComponent(this.createLocationComponent("Hider Spawn", NamedTextColor.AQUA, game.getHiderSpawn())))
                .append(Component.newline())
                .append(this.createBulletedComponent(this.createLocationComponent("Seeker Spawn", NamedTextColor.AQUA, game.getSeekerSpawn())))
                .append(Component.newline())
                .append(this.createBulletedComponent(this.createTeamListComponent("Manage Teams", NamedTextColor.RED, game)))
                .append(Component.newline())
                .append(this.createBulletedComponent(this.createGameControlComponent(game)))
                .append(Component.newline())
                .build();
    }

    @NotNull
    private TextComponent getTeamInfo(Game game, Team team) {
        // TODO: Fix this up, since players are fucked up idk just look at it
        TextComponent.Builder builder = Component.text()
                .append(this.createPrefixedComponent(Component.text("List of players from team ").color(NamedTextColor.WHITE)
                        .append(team.displayName())
                        .append(Component.text(":").color(NamedTextColor.WHITE))))
                .append(Component.newline());

        for (OfflinePlayer targetPlayer : team.getPlayers()) {
            String playerName = targetPlayer.getName();
            builder.append(this.createBulletedComponent(this.createPlayerInfoComponent(playerName, NamedTextColor.WHITE, game)))
                    .append(Component.newline());
        }

        return builder.build();
    }

    private TextComponent getTeamListInfo(Game game) {
        TextComponent.Builder builder = Component.text()
                .append(this.createPrefixedComponent(Component.text("List of teams:").color(NamedTextColor.WHITE)))
                .append(Component.newline());

        for (Team team : game.getTeams()) {
            builder.append(this.createBulletedComponent(this.createTeamInfoComponent(team, game)))
                    .append(Component.newline());
        }

        return builder.build();
    }

    private TextComponent getPlayerInfo(Game game, Player player) {
        return Component.text()
                .append(this.createPrefixedComponent(
                        Component.text()
                                .append(Component.text("Player Info:").color(NamedTextColor.WHITE))
                                .append(Component.space())
                                .append(Component.text("\"" + player.getName() + "\"")).color(NamedTextColor.YELLOW)
                                .build()))
                .append(Component.newline())
                .append(this.createLabelledBulletedComponent("Team",
                        game.getTeam(player) != null ? this.createTeamInfoComponent(game.getTeam(player), game) : Component.text("NULL").color(NamedTextColor.WHITE)))
                .append(Component.newline())
                .append(this.createLabelledBulletedComponent("Tagged", game.isTagged(player) ? Component.text("✔").color(NamedTextColor.GREEN) : Component.text("✘").color(NamedTextColor.RED)))
                .append(Component.newline())
                .append(this.createBulletedComponent(this.createLocationComponent("Location", NamedTextColor.AQUA, player.getLocation())))
                .append(Component.newline())
                .build();
    }

    private TextComponent createCommandComponent(String label, TextColor color, String hoverLabel, String command) {
        return Component.text(label).color(color)
                .hoverEvent(HoverEvent.showText(Component.text(hoverLabel).color(NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand(command));
    }

    private TextComponent createCommandComponent(TextComponent component, String hoverLabel, String command) {
        return component
                .hoverEvent(HoverEvent.showText(Component.text(hoverLabel).color(NamedTextColor.AQUA)))
                .clickEvent(ClickEvent.runCommand(command));
    }

    private TextComponent createPlayerInfoComponent(String playerName, TextColor color, Game game) {
        return this.createCommandComponent(playerName, color, "Click to show player info", "/game manage display-player " + playerName);
    }

    private TextComponent createTeamInfoComponent(Team team, Game game) {
        return this.createCommandComponent((TextComponent) team.displayName().color(team.color()), "Click to show team info", "/game manage teams manage " + team.getName() + " info");
    }

    private TextComponent createTeamListComponent(String label, TextColor color, Game game) {
        return this.createCommandComponent(label, color, "Click to list teams", "/game manage teams list");
    }

    private TextComponent createLocationComponent(String label, TextColor color, Location loc) {
        return this.createCommandComponent(label, color, "Click to teleport", "/tp @s " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
    }

    private TextComponent createPrefixedComponent(TextComponent component) {
        return HideNSeek.getPrefix().append(Component.space()).append(component);
    }

    private TextComponent createBulletedComponent(TextComponent component) {
        return this.createPrefixedComponent(Component.text("> ").color(NamedTextColor.GRAY).append(component));
    }

    private TextComponent createLabelledBulletedComponent(String label, TextComponent component) {
        return this.createBulletedComponent(Component.text(label + ": ").color(NamedTextColor.AQUA).append(component));
    }

    private TextComponent createGameControlComponent(Game game) {
        TextComponent stopComponent = this.createCommandComponent("Stop Game", NamedTextColor.RED, "Click to stop", "/game stop");
        TextComponent startComponent = this.createCommandComponent("Start Game", NamedTextColor.GREEN, "Click to start", "/game start");

        return game.isActive() ? stopComponent : startComponent;
    }
}
