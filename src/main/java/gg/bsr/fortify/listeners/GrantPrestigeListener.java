package gg.bsr.fortify.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.concurrent.atomic.AtomicReference;

public class GrantPrestigeListener implements Listener {
    LuckPerms lp = LuckPermsProvider.get();

    @EventHandler
    public void onPlayer(PlayerAdvancementDoneEvent event) {
        // Return early if player already has prestige
        if (lp == null) return;
        if (event.getPlayer().hasPermission("group.prestige")) return;

        AtomicReference<Boolean> hasAdvancementsLeft = new AtomicReference<>(false);
        Bukkit.advancementIterator().forEachRemaining((advancement) -> {
            if (!event.getPlayer().getAdvancementProgress(advancement).isDone()) {
                hasAdvancementsLeft.set(true);
                return;
            }
        });

        if (!hasAdvancementsLeft.get()) {
            if (event.getPlayer().hasPermission("group.prestige")) return;
            User user = lp.getPlayerAdapter(Player.class).getUser(event.getPlayer());
            user.data().add(Node.builder("group.prestige").build());
            lp.getUserManager().saveUser(user);

            TextComponent textComponent = Component.text("")
                    .append(
                            Component.text("                                                                                \n")
                                    .color(NamedTextColor.DARK_GREEN)
                                    .decoration(TextDecoration.STRIKETHROUGH, true)
                    )
                    .append(event.getPlayer().displayName())
                    .append(
                            Component.text(" completed all advancements and became ")
                                    .color(NamedTextColor.GOLD)
                    )
                    .append(
                            Component.text("| ")
                                    .color(NamedTextColor.RED)
                                    .decoration(TextDecoration.OBFUSCATED, true)
                                    .decoration(TextDecoration.BOLD, true)
                    )
                    .append(
                            Component.text("PRESTIGE ")
                                    .color(NamedTextColor.RED)
                                    .decoration(TextDecoration.BOLD, true)
                    )
                    .append(
                            Component.text("|")
                                    .color(NamedTextColor.RED)
                                    .decoration(TextDecoration.OBFUSCATED, true)
                                    .decoration(TextDecoration.BOLD, true)
                    )
                    .append(
                            Component.text("!")
                                    .color(NamedTextColor.GOLD)
                    )
                    .append(
                            Component.text("\n                                                                                ")
                                    .color(NamedTextColor.DARK_GREEN)
                                    .decoration(TextDecoration.STRIKETHROUGH, true)
                    );

            Bukkit.broadcast(textComponent);
        }
    }
}
