package gg.bsr.fortify;

import gg.bsr.fortify.commands.*;
import gg.bsr.fortify.events.XPEvent;
import gg.bsr.fortify.lib.Config;
import gg.bsr.fortify.listeners.*;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.UUID;

public class Fortify extends JavaPlugin implements Listener {
    LuckPerms lp;

    @Override
    public void onEnable() {
        // Register LuckPerms
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            lp = provider.getProvider();
        }

        saveDefaultConfig();

        // Register commands
//        this.getCommand("showxp").setExecutor(new CommandShowXP());
//        this.getCommand("hidexp").setExecutor(new CommandHideXP());
        this.getCommand("setxp").setExecutor(new CommandSetXP());
        this.getCommand("verify").setExecutor(new CommandVerify());
        this.getCommand("verify").setTabCompleter(new TabCompletionListener());
        this.getCommand("phantom").setExecutor(new CommandPhantom());
        this.getCommand("phantom").setTabCompleter(new TabCompletionListener());

        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new UpdateXPBossBarListener(), this);
        Bukkit.getPluginManager().registerEvents(new GrantPrestigeListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShowXPBossBarListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLevelUpListener(), this);
//        Bukkit.getPluginManager().registerEvents(new EntityResurrectListener(), this);

        new PlayerNodeChangeListener(this, lp).register();

        // Poll API for queued commands
        int interval = new Config(this, "config.yml").getConfig().getInt("queue_interval");

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    pollQueuedCommands();
                } catch (Exception error) {
                    Bukkit.getLogger().warning("Error whilst attempting to poll command queue: " + error);
                }
            }
        }.runTaskTimer(this, 0L, interval);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    sendNotAppliedActionBar();
                } catch (Exception error) {
                    Bukkit.getLogger().warning("Error whilst attempting to send not applied action bar: " + error);
                }
            }
        }.runTaskTimer(this, 0L, 40);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    enforcePhantomSpawning();
                } catch (Exception error) {
                    Bukkit.getLogger().warning("Error whilst attempting to enforce phantom spawning: " + error);
                }
            }
        }.runTaskTimer(this, 0L, 1200);
    }

    private void pollQueuedCommands() throws Exception {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("fortify");
        if (plugin == null) {
            this.getLogger().warning("Fortify plugin unable to be accessed.");
            return;
        }

        String baseUrl = new Config(plugin, "config.yml").getConfig().getString("api_base_url");
        String apiKey = new Config(plugin, "config.yml").getConfig().getString("api_key");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/queue"))
                .headers("Authorization", apiKey)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode != 200) {
            this.getLogger().warning("Non-200 queue poll response body: " + responseBody);
            return;
        }

        String[] commands = responseBody.split("\\R");

        for (String command : commands) {
            if (command.length() != 0) {
                this.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    private void sendNotAppliedActionBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("group.spawn_interaction")) {
                player.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize("&eOnly members can interact near spawn! &aRun /discord to apply."));
            }
        }
    }

    private void enforcePhantomSpawning() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final NamespacedKey phantomKey = new NamespacedKey("fortify", "phantom");
            Integer value = player.getPersistentDataContainer().get(phantomKey, PersistentDataType.INTEGER);

            if (value == null || value == 0) return;
            player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        }
    }
}
