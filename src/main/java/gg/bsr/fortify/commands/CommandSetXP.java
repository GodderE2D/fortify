package gg.bsr.fortify.commands;

import static gg.bsr.fortify.lib.ChatMsg.*;

import gg.bsr.fortify.events.XPEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class CommandSetXP implements CommandExecutor {
    final NamespacedKey xpKey = new NamespacedKey("fortify", "xp");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fortify.xp.setxp")) return true;

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

        player.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, xp);
        sender.sendMessage(info(player.getName() + "'s XP has been set to " + xp + " (Level " + ((int) Math.floor(xp / 20000) + 1) + ")"));

        Integer oldXP = player.getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);
        if (oldXP == null) oldXP = 0;
        Bukkit.getServer().getPluginManager().callEvent(new XPEvent(player, oldXP, xp));

        return true;
    }
}
