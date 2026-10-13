package gg.bsr.fortify.commands;

import static gg.bsr.fortify.lib.ChatMsg.*;
import static gg.bsr.fortify.lib.XPBossBars.barsList;
import static gg.bsr.fortify.lib.XPBossBars.showXPBossBar;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class CommandShowXP implements CommandExecutor {

    static final NamespacedKey showXPBossBarKey = new NamespacedKey("fortify", "showxpbossbar");
    static final NamespacedKey xpKey = new NamespacedKey("fortify", "xp");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(error("You must be a player to run this command!"));
            return true;
        }

        Integer isXPBossBarShown = player.getPersistentDataContainer().get(showXPBossBarKey, PersistentDataType.INTEGER);

        if (isXPBossBarShown != null) {
            if (isXPBossBarShown == 1) {
                sender.sendMessage(error("Your Fortalice XP bar is already shown! To hide it, run /hidexp."));
                return true;
            }
        }

        try {
            showXPBossBar(player);
        } catch (InterruptedException e) {
            Bukkit.getLogger().severe(e.toString());
        }
        sender.sendMessage(info("Your Fortalice XP bar is now shown."));

        return true;
    }
}
