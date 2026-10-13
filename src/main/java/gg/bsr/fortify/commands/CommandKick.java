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

public class CommandKick implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fortify.mod.kick")) return true;

        if (args.length < 2) {
            sender.sendMessage(error("You provided insufficient arguments."));
            return false;
        }
        String playerName = args[0];
        String xpStr = args[1];

        if (Bukkit.getPlayer(playerName) == null) {
            sender.sendMessage(error("You need to provide a valid player name!"));
            return false;
        }

        if (!xpStr.matches("^\\d+$")) {
            sender.sendMessage(error("You need to provide a valid xp amount!"));
            return false;
        }

        int xp = Integer.valueOf(xpStr);

        Player player = Bukkit.getPlayer(playerName);

        sender.sendMessage(info("Your Fortalice XP bar is now shown."));

        return true;
    }
}
