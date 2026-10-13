package gg.bsr.fortify.events;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import static gg.bsr.fortify.lib.XPBossBars.showXPBossBar;

public final class XPEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;
    private Player player;
    private int oldXP;
    private int newXP;

    private final NamespacedKey xpKey = new NamespacedKey("fortify", "xp");

    public XPEvent(Player player, int oldXP, int newXP) {
        this.player = player;
        this.oldXP = oldXP;
        this.newXP = newXP;
    }

    public Player getPlayer() {
        return player;
    }

    public int getOldXP() {
        return oldXP;
    }

    public int getNewXP() {
        return newXP;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
