package net.pl3x.bukkit.chatter.listener;

import net.pl3x.bukkit.chatter.command.CmdReply;
import net.pl3x.bukkit.chatter.configuration.PlayerConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerConfig.getConfig(player).thenAccept(config -> player.setDisplayName(config.getNick()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerConfig.removeConfig(player);
        CmdReply.REPLY_DB.entrySet().removeIf(e ->
                e.getKey() == player || e.getValue() == player);
    }
}
