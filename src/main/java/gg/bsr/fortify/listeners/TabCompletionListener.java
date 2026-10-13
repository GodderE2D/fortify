package gg.bsr.fortify.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class TabCompletionListener implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("phantom")) {
            if (!sender.hasPermission("fortify.phantom")) {
                return Collections.emptyList();
            }

            if (args.length == 1) {
                List<String> subcommands = new ArrayList<>();
                subcommands.add("status");
                subcommands.add("enable");
                subcommands.add("disable");

                return subcommands;
            }
        }

        if (cmd.getName().equalsIgnoreCase("verify")) {
            return Collections.emptyList();
        }

        return null;
    }
}
