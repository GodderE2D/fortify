package gg.bsr.fortify.listeners;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.persistence.PersistentDataType;

public class EntityResurrectListener implements Listener {
    final NamespacedKey key = new NamespacedKey("fortify", "totempops");

    @EventHandler
    public void onPlayerItemConsume(EntityDamageEvent event) {
        Bukkit.getLogger().info("Received");
        if (event.isCancelled()) return;
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (((LivingEntity) event.getEntity()).getHealth() != 0) return;

        Integer totemPops = event.getEntity().getPersistentDataContainer().get(
                key,
                PersistentDataType.INTEGER
        );

        if (totemPops == null) {
            totemPops = 0;
        }

        String message = "§f" + event.getEntity().getName() + " popped their " + ordinal(totemPops + 1) + " §6 Totem of Undying";

        Bukkit.getServer().broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(message));

        event.getEntity().getPersistentDataContainer().set(
                key,
                PersistentDataType.INTEGER,
                totemPops + 1
        );
    }

    private static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }
}
