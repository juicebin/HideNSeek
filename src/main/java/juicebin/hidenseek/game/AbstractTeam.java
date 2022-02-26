package juicebin.hidenseek.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractTeam {
    private final List<UUID> uuidList = new ArrayList<>();
    private final TextColor color;
    private final String id;
    private final Component displayName;

    public AbstractTeam(String id, String displayName, TextColor color) {
        this.id = id;
        this.displayName = Component.text(displayName).color(color);
        this.color = color;
    }

    public void addPlayer(UUID uuid) {
        uuidList.add(uuid);
    }

    public void addPlayer(Player player) {
        uuidList.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        uuidList.remove(player.getUniqueId());
    }

    public List<OfflinePlayer> getOfflinePlayers() {
        return uuidList.stream().map(Bukkit::getOfflinePlayer).toList();
    }

    public List<Player> getOnlinePlayers() {
        return uuidList.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<UUID> getUuidList() {
        return uuidList;
    }

    public boolean hasPlayer(UUID uuid) {
        return uuidList.contains(uuid);
    }

    public TextColor getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    public Component getDisplayName() {
        return displayName;
    }
}
