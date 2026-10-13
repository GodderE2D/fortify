package gg.bsr.fortify.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

import static com.gmail.nossr50.api.ExperienceAPI.getPowerLevel;
import static gg.bsr.fortify.lib.ChatMsg.info;

public class PlayerLevelUpListener implements Listener {
    LuckPerms lp = LuckPermsProvider.get();

    @EventHandler
    public void onPlayerLevelUp(McMMOPlayerLevelUpEvent event) {
        int powerLevel = getPowerLevel(event.getPlayer());
        Player player = event.getPlayer();

        if (powerLevel >= 5000) {
            setHomesPermission(5000, player);
        } else if (powerLevel >= 4000) {
            setHomesPermission(4000, player);
        } else if (powerLevel >= 3000) {
            setHomesPermission(3000, player);
        } else if (powerLevel >= 2000) {
            setHomesPermission(2000, player);
        } else if (powerLevel >= 1000) {
            setHomesPermission(1000, player);
        }
    }

    private void setHomesPermission(int baseLevel, Player player) {
        // If player already has permission, return early
        if (player.hasPermission("essentials.sethome.multiple.mcmmo" + baseLevel)) return;

        Map<Integer, Integer> homesMap = new HashMap<>();
        homesMap.put(1000, 5);
        homesMap.put(2000, 7);
        homesMap.put(3000, 9);
        homesMap.put(4000, 11);
        homesMap.put(5000, 13);

        User user = lp.getPlayerAdapter(Player.class).getUser(player);
        user.data().add(Node.builder("essentials.sethome.multiple.mcmmo" + baseLevel).build());
        lp.getUserManager().saveUser(user);

        player.sendMessage(info("You reached a Power Level of &a" + baseLevel + "&r and can now set &6" + homesMap.get(baseLevel) + "&r homes!"));
    }
}
