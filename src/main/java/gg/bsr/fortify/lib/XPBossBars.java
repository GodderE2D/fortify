package gg.bsr.fortify.lib;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.UUID;

public interface XPBossBars {
    static HashMap<UUID, BossBar> barsList = new HashMap();
    static final NamespacedKey showXPBossBarKey = new NamespacedKey("fortify", "showxpbossbar");
    static final NamespacedKey xpKey = new NamespacedKey("fortify", "xp");

    public static void showXPBossBar(Player player) throws InterruptedException {
        Integer playerXP = player.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);
        if (playerXP == null) playerXP = 0;

        boolean isPrestige = player.hasPermission("group.prestige");

        BossBar bar = BossBar.bossBar(
                LegacyComponentSerializer.legacyAmpersand().deserialize((isPrestige ? "&c&l&k| &c&lPRESTIGE &k|&r " : "") + "Fortalice Level &a" + (int) (Math.floor(playerXP / 20000.0) + 1)),
                0,
                BossBar.Color.GREEN,
                BossBar.Overlay.PROGRESS
        );

        bar.progress(getLevelProgress(playerXP));

        player.showBossBar(bar);
        barsList.put(player.getUniqueId(), bar);
        player.getPersistentDataContainer().set(
                showXPBossBarKey,
                PersistentDataType.INTEGER,
                1
        );
    }

    static float getLevelProgress(int playerXP) {
        int currentLevelXP = playerXP % 20000;
        float progress = (float) currentLevelXP / 20000.0f;
        return progress;
    }
}
