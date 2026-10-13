package gg.bsr.fortify.commands;

import static gg.bsr.fortify.lib.ChatMsg.*;
import static gg.bsr.fortify.lib.XPBossBars.barsList;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class CommandHideXP implements CommandExecutor {

    final NamespacedKey key = new NamespacedKey("fortify", "showxpbossbar");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(error("You must be a player to run this command!"));
            return true;
        }

        Integer isXPBossBarShown = player.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);

        if (isXPBossBarShown == null || isXPBossBarShown == 0) {
            sender.sendMessage(error("Your Fortalice XP bar is already hidden! To show it, run /showxp."));
            return true;
        }

        player.hideBossBar(barsList.get(player.getUniqueId()));
        barsList.remove(player.getUniqueId());
        player.getPersistentDataContainer().set(
                key,
                PersistentDataType.INTEGER,
                0
        );

        sender.sendMessage(info("Your Fortalice XP bar is now hidden."));

        return true;
    }
}
