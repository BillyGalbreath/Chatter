package net.pl3x.bukkit.chatter.listener;

import net.pl3x.bukkit.chatter.Chatter;
import net.pl3x.bukkit.chatter.configuration.Lang;
import net.pl3x.bukkit.chatter.configuration.PlayerConfig;
import net.pl3x.bukkit.chatter.hook.Vault;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();

        try {
            if (PlayerConfig.getConfig(sender).get().isMuted()) {
                Lang.send(sender, Lang.YOU_ARE_MUTED);
                event.setCancelled(true);
                return;
            }
        } catch (InterruptedException | ExecutionException ex) {
            Chatter.getInstance().getLogger().log(Level.WARNING, "Could not determine if player is muted. Cancelling message.", ex);
            event.setCancelled(true);
            return;
        }

        event.setFormat(ChatColor.translateAlternateColorCodes('&',
                Lang.CHAT_FORMAT
                        .replace("{sender}", "%1$s")
                        .replace("{message}", "%2$s")
                        .replace("{prefix}", Vault.getPrefix(sender))
                        .replace("{suffix}", Vault.getSuffix(sender))
                        .replace("{group}", WordUtils.capitalizeFully(Vault.getPrimaryGroup(sender)))
                        .replace("{world}", sender.getWorld().getName())
                )
        );

        event.setMessage(checkColorPerms(sender, event.getMessage()));
    }

    private static String checkColorPerms(Player player, String message) {
        if (message != null) {
            message = ChatColor.translateAlternateColorCodes('&', message);
            if (!player.hasPermission("chat.color")) {
                message = message.replaceAll("(?i)\u00A7[0-9a-f]", "");
            }
            if (!player.hasPermission("chat.style")) {
                message = message.replaceAll("(?i)\u00A7[l-o]", "");
            }
            if (!player.hasPermission("chat.magic")) {
                message = message.replaceAll("(?i)\u00A7[k]", "");
            }
        }
        return message;
    }
}
