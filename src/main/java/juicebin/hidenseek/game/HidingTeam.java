package juicebin.hidenseek.game;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HidingTeam extends AbstractTeam {
    private final List<UUID> inactivePlayerList = new ArrayList<>();
    private boolean active;

    public HidingTeam(String id, String displayName, TextColor color) {
        super(id, displayName, color);
        this.active = false;
    }

    public List<UUID> getInactivePlayers() {
        return inactivePlayerList;
    }

    public void setPlayerActive(Player player, boolean active) {
        if (active) {
            inactivePlayerList.add(player.getUniqueId());
        } else {
            inactivePlayerList.remove(player.getUniqueId());
        }
    }

    public void setPlayerActive(OfflinePlayer player, boolean active) {
        if (active) {
            inactivePlayerList.add(player.getUniqueId());
        } else {
            inactivePlayerList.remove(player.getUniqueId());
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

}
