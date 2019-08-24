package net.pl3x.bukkit.chatter.listener;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import net.pl3x.bukkit.chatter.Chatter;
import net.pl3x.bukkit.chatter.configuration.Lang;
import net.pl3x.bukkit.chatter.configuration.PlayerConfig;
import net.pl3x.bukkit.chatter.hook.LuckPermsHook;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        String message = checkColorPerms(sender, event.getMessage());

        String format = ChatColor.translateAlternateColorCodes('&', Lang.CHAT_FORMAT
                .replace("{prefix}", LuckPermsHook.getPrefix(sender))
                .replace("{suffix}", LuckPermsHook.getSuffix(sender))
                .replace("{group}", WordUtils.capitalizeFully(LuckPermsHook.getPrimaryGroup(sender)))
                .replace("{world}", sender.getWorld().getName())
        ).replace("{message}", message);

        String[] formatParts = format.split("\\{sender}");

        BaseComponent[] componentsBeforeName = TextComponent.fromLegacyText(formatParts[0]);
        BaseComponent[] componentsAfterName;

        if (message.contains("[item]")) {
            ItemStack itemInHand = sender.getInventory().getItemInMainHand().clone();

            // remove all page content from books (to prevent client crashes from string too big)
            if (itemInHand.getType() == Material.WRITABLE_BOOK || itemInHand.getType() == Material.WRITTEN_BOOK) {
                BookMeta meta = (BookMeta) itemInHand.getItemMeta();
                meta.setPages(new ArrayList<>());
                itemInHand.setItemMeta(meta);
            }

            net.minecraft.server.v1_14_R1.ItemStack nmsItemClone = CraftItemStack.asNMSCopy(itemInHand);
            NBTTagCompound itemNBTTag = nmsItemClone.save(new NBTTagCompound());

            String itemName = getItemName(itemInHand);
            if (itemInHand.getAmount() > 1) {
                itemName = Lang.ITEM_FORMAT_MULTI
                        .replace("{item}", itemName)
                        .replace("{amount}", Integer.toString(itemInHand.getAmount()));
            } else {
                itemName = Lang.ITEM_FORMAT.replace("{item}", itemName);
            }
            String itemTag = itemNBTTag.toString();

            String[] itemParts = formatParts[1].split("\\[item]");

            BaseComponent[] componentsBeforeItem = TextComponent.fromLegacyText(itemParts[0]);
            BaseComponent[] componentsAfterItem = TextComponent.fromLegacyText(String.join(" ", Arrays.copyOfRange(itemParts, 1, itemParts.length)));

            BaseComponent[] itemComponents = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', ChatColor.getLastColors(itemParts[0]) + itemName));
            BaseComponent[] itemHoverComponents = TextComponent.fromLegacyText(itemTag);
            for (BaseComponent itemComponent : itemComponents) {
                itemComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemHoverComponents));
            }

            List<BaseComponent> componentsRebuilt = new ArrayList<>();
            Collections.addAll(componentsRebuilt, componentsBeforeItem);
            Collections.addAll(componentsRebuilt, itemComponents);
            Collections.addAll(componentsRebuilt, componentsAfterItem);
            componentsAfterName = componentsRebuilt.toArray(new BaseComponent[0]);

            // clean it up for console
            String item = "";

            ItemMeta meta = null;
            if (itemInHand.hasItemMeta()) {
                meta = itemInHand.getItemMeta();
            }

            Map<Enchantment, Integer> enchants = new HashMap<>(itemInHand.getEnchantments());
            if (itemInHand.getType() == Material.ENCHANTED_BOOK) {
                enchants.putAll(((EnchantmentStorageMeta) itemInHand.getItemMeta()).getStoredEnchants());
            }
            if (!enchants.isEmpty() && (meta == null || !meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS))) {
                StringBuilder enchantTags = new StringBuilder();
                for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                    enchantTags.append("\n    ").append(((CraftEnchantment) entry.getKey()).getHandle().d(entry.getValue()).e());
                }
                item = item + "\n  Enchantments:" + enchantTags;
            }

            if (meta != null && meta.hasLore()) {
                StringBuilder loreTags = new StringBuilder();
                for (String lore : meta.getLore()) {
                    loreTags.append("\n    ").append(lore);
                }
                item = item + "\n  Lore:" + loreTags;
            }

            message = "&r" + ChatColor.stripColor(message)
                    .replace("[item]", itemName + "&r") +
                    (item.isEmpty() ? "" : "\n```\n" + itemName
                            .replace("[", "")
                            .replace("]", "") +
                            "&r:" + item + "\n```");
        } else {
            componentsAfterName = TextComponent.fromLegacyText(String.join(" ", Arrays.copyOfRange(formatParts, 1, formatParts.length)));
        }

        TextComponent senderComponents = new TextComponent(ChatColor.getLastColors(formatParts[0]) +
                sender.getDisplayName());
        senderComponents.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, Lang.CLICK_SUGGEST_COMMAND
                .replace("{name}", sender.getName())
                .replace("{display-name}", sender.getDisplayName())));

        senderComponents.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', Lang.HOVER_TOOLTIP
                        .replace("{prefix}", LuckPermsHook.getPrefix(sender))
                        .replace("{suffix}", LuckPermsHook.getSuffix(sender))
                        .replace("{group}", WordUtils.capitalizeFully(LuckPermsHook.getPrimaryGroup(sender)))
                        .replace("{name}", sender.getName())
                        .replace("{display-name}", sender.getDisplayName())))));

        List<BaseComponent> componentsList = new ArrayList<>();
        componentsList.addAll(Arrays.asList(componentsBeforeName));
        componentsList.add(senderComponents);
        componentsList.addAll(Arrays.asList(componentsAfterName));

        BaseComponent[] componentsFinalized = new BaseComponent[componentsList.size()];
        componentsList.toArray(componentsFinalized);

        for (Player recipient : event.getRecipients()) {
            recipient.sendMessage(componentsFinalized);
        }

        event.getRecipients().clear();
        event.setMessage(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)));
    }

    private String checkColorPerms(Player player, String message) {
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

    private String getItemName(ItemStack itemStack) {
        if (itemStack == null) {
            return "";
        }
        if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        String name = Bukkit.getItemFactory().getI18NDisplayName(itemStack);
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return WordUtils.capitalizeFully(itemStack.getType().name().replace("_", " ").toLowerCase());
    }
}
