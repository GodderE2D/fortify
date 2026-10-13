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

public class CommandPhantom implements CommandExecutor {
    final NamespacedKey phantomKey = new NamespacedKey("fortify", "phantom");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fortify.phantom")) {
            sender.sendMessage(error("You must be a Supporter to use this command!"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(error("You must be a player to run this command!"));
            return true;
        }

        if (args.length != 1 || args[0].equals("status")) {
            Integer value = player.getPersistentDataContainer().get(phantomKey, PersistentDataType.INTEGER);

            if (value == null) {
                player.getPersistentDataContainer().set(phantomKey, PersistentDataType.INTEGER, 0);
                sender.sendMessage(info("Phantoms are spawning because of you, to disable spawning run /phantom disable."));
                return true;
            }

            if (value == 0) {
                sender.sendMessage(info("Phantoms are spawning because of you, to disable spawning run /phantom disable."));
                return true;
            }

            sender.sendMessage(info("Phantoms are not spawning because of you, to enable spawning run /phantom enable."));
            return true;
        }

        if (args[0].equals("enable")) {
            player.getPersistentDataContainer().set(phantomKey, PersistentDataType.INTEGER, 0);
            sender.sendMessage(info("Phantoms will now spawn because of you, to disable spawning run /phantom disable."));
            return true;
        }

        if (args[0].equals("disable")) {
            player.getPersistentDataContainer().set(phantomKey, PersistentDataType.INTEGER, 1);
            sender.sendMessage(info("Phantoms will no longer spawn because of you, to enable spawning run /phantom enable."));
            return true;
        }

        sender.sendMessage(error("Valid arguments are: status, enable, disable"));
        return true;
    }
}
