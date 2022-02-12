package juicebin.hidenseek.event;

import juicebin.hidenseek.game.Game;
import org.bukkit.entity.Player;

public class SeekerTagHiderEvent extends GameEvent {
    private final Player seeker;
    private final Player hider;

    public SeekerTagHiderEvent(Game game, Player seeker, Player hider) {
        super(game);
        this.seeker = seeker;
        this.hider = hider;
    }

    public Player getSeeker() {
        return seeker;
    }

    public Player getHider() {
        return hider;
    }
}
