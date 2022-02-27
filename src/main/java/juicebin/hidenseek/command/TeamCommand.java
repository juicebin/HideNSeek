package juicebin.hidenseek.command;

import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.game.AbstractTeam;
import juicebin.hidenseek.game.Game;
import juicebin.hidenseek.game.HidingTeam;
import juicebin.hidenseek.util.ComponentUtils;
import juicebin.hidenseek.util.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TeamCommand implements CommandExecutor {
    private final HideNSeek plugin;

    public TeamCommand(HideNSeek instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //      /team list
        //      /team view-player <player>

        if (args.length == 0) return false;

        Game game = plugin.getGame();

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "list" -> {
                if (args.length != 1) return false;

                MessageUtils.sendMessage(sender, this.createTeamListComponent(game));
                return true;
            }
            case "view-team" -> {
                if (args.length != 2) return false;

                AbstractTeam team = game.getTeam(args[1]);

                MessageUtils.sendMessage(sender, this.createTeamInfoComponent(team));
                return true;
            }
            case "view-player" -> {
                if (args.length != 2) return false;

                UUID uuid = UUID.fromString(args[1]);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                MessageUtils.sendMessage(sender, this.createPlayerInfoComponent(offlinePlayer, game));

                return true;
            }
            case "view-players" -> {
                if (args.length != 1) return false;

                MessageUtils.sendMessage(sender, this.createPlayerListComponent(game));

                return true;
            }
        }

        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    private TextComponent createTeamListComponent(Game game) {
        //      [H&S] Team List: [+NEW]
        //      [H&S] - Team Red
        //      [H&S] - Team Indigo

        TextComponent.Builder builder = Component.text()
                .append(ComponentUtils.createPrefixedComponent(Component.text("Team List: ").color(NamedTextColor.WHITE)))
                .append(Component.newline())
                .append(ComponentUtils.createBulletedComponent(this.createTeamCommandComponent(game.getSeekingTeam())));

        for (HidingTeam team : game.getHidingTeams()) {
            builder.append(Component.newline());
            builder.append(ComponentUtils.createBulletedComponent(this.createTeamCommandComponent(team)));
        }

        return builder.build();
    }

    private TextComponent createTeamCommandComponent(AbstractTeam team) {
        return ComponentUtils.createCommandComponent(team.getDisplayName(), "Click to view team info", "/hs team manage " + team.getId() + " view");
    }

    private TextComponent createTeamInfoComponent(AbstractTeam team) {
        //      [H&S] Team Info: Team Red
        //      [H&S] 2 Players
        //      [H&S] - juicebin
        //      [H&S] - DotWavJordan

        TextComponent.Builder builder = Component.text()
                .append(ComponentUtils.createPrefixedComponent(Component.text("Team Info: ").color(NamedTextColor.WHITE)))
                .append(team.getDisplayName())
                .append(Component.newline())
                .append(ComponentUtils.createPrefixedComponent(Component.text(team.getPlayers().size() + " Players").color(NamedTextColor.AQUA)));

        for (OfflinePlayer player : team.getPlayers()) {
            builder.append(Component.newline());
            builder.append(ComponentUtils.createBulletedComponent(this.createPlayerCommandComponent(player)));
        }

        return builder.build();
    }

    private TextComponent createPlayerCommandComponent(OfflinePlayer player) {
        String name = player.getName() != null ? player.getName() : "NULL";
        NamedTextColor color = player.isOnline() ? NamedTextColor.GREEN : NamedTextColor.RED;
        return ComponentUtils.createCommandComponent(name, color, "Click to display player info", "/hs team view-player " + player.getUniqueId());
    }

    private TextComponent createPlayerInfoComponent(OfflinePlayer player, Game game) {
        //      [H&S] Player Info: juicebin
        //      [H&S] - Team: Team Red
        //      [H&S] - Status: Tagged

        String name = player.getName() != null ? player.getName() : "OFFLINE";

        return Component.text()
                .append(ComponentUtils.createPrefixedComponent(Component.text("Player Info: ")))
                .append(Component.text(name).color(NamedTextColor.AQUA))
                .append(Component.newline())
                .append(ComponentUtils.createBulletedComponent(Component.text("Team: ").color(NamedTextColor.WHITE)))
                .append(this.createTeamCommandComponent(game.getTeam(player)))
                .append(Component.newline())
                .append(ComponentUtils.createBulletedComponent(Component.text("Status: ").color(NamedTextColor.WHITE)))
                .append(game.isTagged(player) ? Component.text("TAGGED").color(NamedTextColor.RED) : Component.text("NOT TAGGED").color(NamedTextColor.GREEN))
                .build();
    }

    private TextComponent createPlayerListComponent(Game game) {
        TextComponent.Builder builder = Component.text()
                .append(ComponentUtils.createPrefixedComponent(Component.text("Player List: ").color(NamedTextColor.WHITE)));

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            builder.append(Component.newline());
            builder.append(ComponentUtils.createBulletedComponent(this.createPlayerCommandComponent(player)));
        }

        return builder.build();
    }
}
