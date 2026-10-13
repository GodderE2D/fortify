package gg.bsr.fortify.listeners;

import static gg.bsr.fortify.lib.XPBossBars.showXPBossBar;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

public class ShowXPBossBarListener implements Listener {
    final NamespacedKey key = new NamespacedKey("fortify", "showxpbossbar");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Integer shouldShowXPBossBar = event.getPlayer().getPersistentDataContainer().get(
                key,
                PersistentDataType.INTEGER
        );

        if (shouldShowXPBossBar == null) return;
        if (shouldShowXPBossBar != 1) return;

        try {
            showXPBossBar(event.getPlayer());
        } catch (InterruptedException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }
}
