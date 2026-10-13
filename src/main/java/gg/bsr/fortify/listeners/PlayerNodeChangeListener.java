package gg.bsr.fortify.listeners;

import gg.bsr.fortify.Fortify;
import gg.bsr.fortify.lib.Config;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class PlayerNodeChangeListener {
    private final Fortify plugin;
    private final LuckPerms lp;

    public PlayerNodeChangeListener(Fortify plugin, LuckPerms lp) {
        this.plugin = plugin;
        this.lp = lp;
    }

    public void register() {
        EventBus eventBus = this.lp.getEventBus();
        eventBus.subscribe(this.plugin, NodeAddEvent.class, this::onNodeAdd);
        eventBus.subscribe(this.plugin, NodeRemoveEvent.class, this::onNodeRemove);
    }

    private void onNodeAdd(NodeAddEvent e) {
        if (!e.isUser()) {
            return;
        }

        UUID uuid = ((User) e.getTarget()).getUniqueId();
        Node node = e.getNode();

        if (!(node instanceof InheritanceNode)) {
            return;
        }

        // LuckPerms events are posted async, we want to process on the server thread!
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            String groupName = ((InheritanceNode) node).getGroupName();
            addOrRemoveRole(true, uuid, groupName);
        });
    }

    private void onNodeRemove(NodeRemoveEvent e) {
        if (!e.isUser()) {
            return;
        }

        UUID uuid = ((User) e.getTarget()).getUniqueId();
        Node node = e.getNode();

        if (!(node instanceof InheritanceNode)) {
            return;
        }

        // LuckPerms events are posted async, we want to process on the server thread!
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            String groupName = ((InheritanceNode) node).getGroupName();
            addOrRemoveRole(false, uuid, groupName);
        });
    }

    private void addOrRemoveRole(boolean add, UUID uuid, String groupName) {
        try {
            String baseUrl = new Config(plugin, "config.yml").getConfig().getString("api_base_url");
            String apiKey = new Config(plugin, "config.yml").getConfig().getString("api_key");

            String jsonBody = "{ \"uuid\": \"" + uuid + "\", \"role\": \"" + groupName + "\", \"add\": " + (add ? "true" : "false") + " }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + "/addOrRemoveRole"))
                    .headers("Authorization", apiKey)
                    .headers("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode != 200) {
                this.plugin.getLogger().warning("Non-200 addOrRemoveRole response body: " + responseBody);
            }
        } catch (Exception err) {
            this.plugin.getLogger().severe("Exception in PlayerNodeChangeListener:" + err);
        }
    }
}
