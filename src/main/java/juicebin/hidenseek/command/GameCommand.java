package juicebin.hidenseek.command;

import juicebin.hidenseek.HideNSeek;
import juicebin.hidenseek.game.Game;
import juicebin.hidenseek.util.ComponentUtils;
import juicebin.hidenseek.util.MessageLevel;
import juicebin.hidenseek.util.MessageUtils;
import juicebin.hidenseek.util.SoundUtils;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Locale;

public class GameCommand implements CommandExecutor {
    private final HideNSeek plugin;

    public GameCommand(HideNSeek instance) {
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;

        Game game = plugin.getGame();

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "start" -> {
                if (args.length != 1) return false;

                if (game.isActive()) {
                    MessageUtils.sendMessage(sender, MessageLevel.ERROR, "Game is already started.");
                    return true;
                }

                // Countdown
                int length = 10;
                new BukkitRunnable() {
                    int i = length;

                    @Override
                    public void run() {
                        MessageUtils.broadcast("&cGame starting in &e" + i + "&c...");
                        SoundUtils.broadcastSound(Sound.sound(org.bukkit.Sound.ENTITY_ARROW_HIT_PLAYER, Sound.Source.MASTER, 0.5f, 1.0f));

                        if (--i <= 0) {
                            game.start();
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 20);
                return true;
            }
            case "stop" -> {
                if (args.length != 1) return false;

                if (!game.isActive()) {
                    MessageUtils.sendMessage(sender, MessageLevel.ERROR, "Game hasn't been started.");
                    return true;
                }

                // Countdown
                int length = 10;
                new BukkitRunnable() {
                    int i = length;

                    @Override
                    public void run() {
                        MessageUtils.broadcast("&cGame force stopping in &6" + i + "&c...");
                        SoundUtils.broadcastSound(Sound.sound(org.bukkit.Sound.ENTITY_ARROW_HIT_PLAYER, Sound.Source.MASTER, 0.5f, 1.0f));

                        if (--i <= 0) {
                            game.stop(true);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 20);
                return true;
            }
            case "view" -> {
                if (args.length != 1) return false;

                MessageUtils.sendMessage(sender, this.createGameInfoComponent(game));
                return true;
            }
        }

        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("start", "stop", "view");
        }
        return List.of();
    }

    private TextComponent createGameControllerComponent() {
        return Component.text()
                .append(Component.text("[").color(NamedTextColor.WHITE))
                .append(ComponentUtils.createCommandComponent("START", NamedTextColor.GREEN, "Click to start game", "/hs game start"))
                .append(Component.text("]").color(NamedTextColor.WHITE))
                .append(Component.space())
                .append(Component.text("|").color(NamedTextColor.WHITE))
                .append(Component.space())
                .append(Component.text("[").color(NamedTextColor.WHITE))
                .append(ComponentUtils.createCommandComponent("STOP", NamedTextColor.RED, "Click to stop game", "/hs game stop"))
                .append(Component.text("]").color(NamedTextColor.WHITE))
                .build();
    }

    private TextComponent createGameInfoComponent(Game game) {
        //      [H&S] Game Controller: [START] | [STOP]
        //      [H&S] - TP to Hider Spawn
        //      [H&S] - TP to Seeker Spawn
        //      [H&S] - Manage Teams

        return Component.text()
                .append(ComponentUtils.createPrefixedComponent(Component.text("Game Controller: ").color(NamedTextColor.WHITE)))
                .append(this.createGameControllerComponent())
                .append(Component.newline())
                .append(ComponentUtils.createBulletedComponent(ComponentUtils.createLocationComponent("TP to Hider Spawn", NamedTextColor.AQUA, game.getHiderSpawn())))
                .append(Component.newline())
                .append(ComponentUtils.createBulletedComponent(ComponentUtils.createLocationComponent("TP to Seeker Spawn", NamedTextColor.AQUA, game.getSeekerSpawn())))
                .append(Component.newline())
                .append(ComponentUtils.createBulletedComponent(ComponentUtils.createCommandComponent("View Teams", NamedTextColor.RED, "Click to manage teams", "/hs team list")))
                .append(Component.newline())
                .append(ComponentUtils.createBulletedComponent(ComponentUtils.createCommandComponent("View Players", NamedTextColor.DARK_GREEN, "Click to manage teams", "/hs team view-players")))
                .build();
    }
}
