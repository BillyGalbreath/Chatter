package net.pl3x.bukkit.chatter.listener;

import net.pl3x.bukkit.chatter.Chatter;
import net.pl3x.bukkit.chatter.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class RacismListener implements Listener {
    private final Chatter plugin;

    public RacismListener(Chatter plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (containsRacism(event.getMessage().toLowerCase())) {
            event.setCancelled(true);
            event.setMessage("");
            event.setFormat("");
            event.getRecipients().clear();
            processBan(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
        for (String line : event.getLines()) {
            if (containsRacism(line)) {
                event.setCancelled(true);
                for (int i = 0; i < 4; i++) {
                    event.setLine(i, "");
                }
                processBan(event.getPlayer());
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBookEdit(PlayerEditBookEvent event) {
        BookMeta meta = event.getNewBookMeta();
        if (containsRacism(meta.getTitle())) {
            event.setCancelled(true);
            event.setNewBookMeta(event.getPreviousBookMeta());
            processBan(event.getPlayer());
            return;
        }
        for (String page : meta.getPages()) {
            if (containsRacism(page)) {
                event.setCancelled(true);
                event.setNewBookMeta(event.getPreviousBookMeta());
                processBan(event.getPlayer());
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (!(inv instanceof AnvilInventory)) {
            return;
        }

        InventoryView view = event.getView();
        int rawSlot = event.getRawSlot();
        if (rawSlot != 2 || rawSlot != view.convertSlot(rawSlot)) {
            return;
        }

        ItemStack result = event.getCurrentItem();
        if (result == null || !result.hasItemMeta()) {
            return;
        }

        ItemMeta meta = result.getItemMeta();
        if (meta.hasDisplayName() && containsRacism(meta.getDisplayName())) {
            event.setCancelled(true);
            event.setCurrentItem(null);
            event.setCursor(null);
            processBan((Player) event.getWhoClicked());
        }
    }

    private boolean containsRacism(String str) {
        if (str == null) {
            return false;
        }
        str = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', str.toLowerCase()
                .replace(" ", "")
                .replace("-", "")
                .replace(".", "")
                .replace(",", "")
        ));
        return Config.RACISM.parallelStream().anyMatch(str::contains);
    }

    private void processBan(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    processBanCommand(player);
                }
            }.runTask(plugin);
        } else {
            processBanCommand(player);
        }
    }

    private void processBanCommand(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Config.AUTO_BAN_RACISM
                .replace("{player}", player.getName()));
    }
}
