package juicebin.hidenseek.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractTeam {
    protected final List<OfflinePlayer> playerList = new ArrayList<>();
    protected final TextColor color;
    protected final String id;
    protected final TextComponent displayName;

    public AbstractTeam(String id, String displayName, TextColor color) {
        this.id = id;
        this.displayName = Component.text(displayName).color(color);
        this.color = color;
    }

    public void addPlayer(OfflinePlayer player) {
        playerList.add(player);
    }

    public void removePlayer(Player player) {
        playerList.remove(player.getUniqueId());
    }

    public List<Player> getOnlinePlayers() {
        return playerList.stream()
                .filter(OfflinePlayer::isOnline)
                .map(OfflinePlayer::getPlayer)
                .toList();
    }

    public List<OfflinePlayer> getPlayers() {
        return playerList;
    }

    public boolean hasPlayer(UUID uuid) {
        return playerList.contains(uuid);
    }

    public TextColor getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    public TextComponent getDisplayName() {
        return displayName;
    }
}
