package net.pl3x.bukkit.chatter.listener;

import net.pl3x.bukkit.chatter.configuration.PlayerConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerConfig.getConfig(player).thenAccept(config -> player.setDisplayName(config.getNick()));
    }
}
