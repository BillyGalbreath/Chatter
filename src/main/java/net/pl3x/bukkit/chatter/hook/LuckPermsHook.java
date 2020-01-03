package net.pl3x.bukkit.chatter.hook;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import net.pl3x.bukkit.chatter.configuration.Config;
import org.bukkit.entity.Player;

public class LuckPermsHook {
    private static LuckPerms API = LuckPermsProvider.get();

    public static String getPrefix(Player player) {
        User user = API.getUserManager().getUser(player.getUniqueId());
        ImmutableContextSet context = API.getContextManager().getContext(user).orElseThrow(() ->
                new IllegalStateException("Could not get LuckPerms context for player " + player));
        QueryOptions query = QueryOptions.contextual(context);
        String prefix;
        if (Config.INHERIT_PREFIXES_FROM_ALL_GROUPS) {
            prefix = String.join("", user.getCachedData().getMetaData(query).getPrefixes().values());
        } else {
            prefix = user.getCachedData().getMetaData(query).getPrefix();
        }
        return prefix == null ? "" : prefix;
    }

    public static String getSuffix(Player player) {
        User user = API.getUserManager().getUser(player.getUniqueId());
        ImmutableContextSet context = API.getContextManager().getContext(user).orElseThrow(() ->
                new IllegalStateException("Could not get LuckPerms context for player " + player));
        QueryOptions query = QueryOptions.contextual(context);
        String suffix;
        if (Config.INHERIT_SUFFIXES_FROM_ALL_GROUPS) {
            suffix = String.join("", user.getCachedData().getMetaData(query).getSuffixes().values());
        } else {
            suffix = user.getCachedData().getMetaData(query).getSuffix();
        }
        return suffix == null ? "" : suffix;
    }

    public static String getPrimaryGroup(Player player) {
        return API.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup();
    }
}
