package gg.bsr.fortify.commands;

import static gg.bsr.fortify.lib.ChatMsg.*;

import gg.bsr.fortify.lib.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.UUID;

public class CommandVerify implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("fortify.verify")) return true;

        if (!(sender instanceof Player player)) {
            sender.sendMessage(error("You must be a player to run this command!"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(error("You provided insufficient arguments."));
            return true;
        }

        try {
            int code = Integer.parseInt(args[0]);
            String message = verifyMember(player.getUniqueId(), code);
            sender.sendMessage(info(message));
        } catch (Exception err) {
            Bukkit.getLogger().severe(err.toString());
            err.printStackTrace();
            sender.sendMessage(error("You provided an invalid verification code, or something else went wrong."));
        }

        return true;
    }

    private String verifyMember(UUID uuid, int code) throws Exception {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("fortify");
        if (plugin == null) return "Something went wrong, plugin was unable to be fetched. Please inform a staff member.";

        String baseUrl = new Config(plugin, "config.yml").getConfig().getString("api_base_url");
        String apiKey = new Config(plugin, "config.yml").getConfig().getString("api_key");

        String jsonBody = "{ \"uuid\": \"" + uuid + "\", \"code\": " + code + " }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/verify"))
                .headers("Authorization", apiKey)
                .headers("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode != 200) return "Something went wrong, please inform a staff member.";
        if (Objects.equals(responseBody, "ALREADY_VERIFIED")) {
            return "You are already verified!";
        }
        return "Your Minecraft account has been verified. Head back to Discord to complete the application process.";
    }
}
