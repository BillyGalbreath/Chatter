package net.pl3x.bukkit.chatter.hook;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.User;
import net.pl3x.bukkit.chatter.configuration.Config;
import org.bukkit.entity.Player;

public class LuckPermsHook {
    public static String getPrefix(Player player) {
        User user = LuckPerms.getApi().getUser(player.getUniqueId());
        Contexts context = LuckPerms.getApi().getContextForUser(user).orElseThrow(() ->
                new IllegalStateException("Could not get LuckPerms context for player " + player));
        if (Config.INHERIT_PREFIXES_FROM_ALL_GROUPS) {
            return String.join("", user.getCachedData().getMetaData(context).getPrefixes().values());
        } else {
            return user.getCachedData().getMetaData(context).getPrefix();
        }
    }

    public static String getSuffix(Player player) {
        User user = LuckPerms.getApi().getUser(player.getUniqueId());
        Contexts context = LuckPerms.getApi().getContextForUser(user).orElseThrow(() ->
                new IllegalStateException("Could not get LuckPerms context for player " + player));
        if (Config.INHERIT_SUFFIXES_FROM_ALL_GROUPS) {
            return String.join("", user.getCachedData().getMetaData(context).getSuffixes().values());
        } else {
            return user.getCachedData().getMetaData(context).getSuffix();
        }
    }

    public static String getPrimaryGroup(Player player) {
        return LuckPerms.getApi().getUser(player.getUniqueId()).getPrimaryGroup();
    }
}
