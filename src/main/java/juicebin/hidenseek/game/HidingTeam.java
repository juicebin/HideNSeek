package juicebin.hidenseek.game;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HidingTeam extends AbstractTeam {
    private final List<OfflinePlayer> inactivePlayerList = new ArrayList<>();
    private boolean active;

    public HidingTeam(String id, String displayName, TextColor color) {
        super(id, displayName, color);
        this.active = false;
    }

    public List<OfflinePlayer> getInactivePlayers() {
        return inactivePlayerList;
    }

    public List<OfflinePlayer> getActivePlayers() {
        return this.playerList.stream().filter(p -> !inactivePlayerList.contains(p)).toList();
    }

    public void setPlayerActive(OfflinePlayer player, boolean active) {
        if (!active) {
            inactivePlayerList.add(player);
        } else {
            inactivePlayerList.remove(player);
        }
    }

    public boolean getPlayerActive(OfflinePlayer player) {
        return !inactivePlayerList.contains(player);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

}
