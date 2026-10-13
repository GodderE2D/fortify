package gg.bsr.fortify.listeners;

import gg.bsr.fortify.events.XPEvent;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import static gg.bsr.fortify.lib.XPBossBars.barsList;
import static gg.bsr.fortify.lib.XPBossBars.showXPBossBar;

public class UpdateXPBossBarListener implements Listener {
    final NamespacedKey key = new NamespacedKey("fortify", "showxpbossbar");

    @EventHandler
    public void onXPUpdate(XPEvent event) {
        Integer shouldShowXPBossBar = event.getPlayer().getPersistentDataContainer().get(
                key,
                PersistentDataType.INTEGER
        );

        if (shouldShowXPBossBar == null) return;
        if (shouldShowXPBossBar != 1) return;

        BossBar oldBar = barsList.get(event.getPlayer().getUniqueId());
        event.getPlayer().hideBossBar(oldBar);

        try {
            showXPBossBar(event.getPlayer());
        } catch (InterruptedException e) {
            Bukkit.getLogger().severe(e.toString());
        }
    }
}
