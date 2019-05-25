package net.pl3x.bukkit.chatter.hook;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {
    private static Permission permission = null;
    private static Chat chat = null;

    public static boolean setupPermissions() {
        RegisteredServiceProvider<Permission> provider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (provider != null) {
            permission = provider.getProvider();
        }
        return permission != null;
    }

    public static boolean setupChat() {
        RegisteredServiceProvider<Chat> provider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (provider != null) {
            chat = provider.getProvider();
        }
        return chat != null;
    }

    public static String getPrefix(Player player) {
        StringBuilder prefixes = new StringBuilder();
        for (String groupName : permission.getPlayerGroups(player)) {
            prefixes.append(chat.getGroupPrefix(player.getWorld(), groupName));
        }
        return prefixes.toString();
    }

    public static String getSuffix(Player player) {
        StringBuilder suffixes = new StringBuilder();
        for (String groupName : permission.getPlayerGroups(player)) {
            suffixes.append(chat.getGroupSuffix(player.getWorld(), groupName));
        }
        return suffixes.toString();
    }

    public static String getPrimaryGroup(Player player) {
        return permission.getPrimaryGroup(player);
    }
}
