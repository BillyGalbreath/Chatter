package net.pl3x.bukkit.chatter.configuration;

import com.google.common.base.Throwables;
import net.pl3x.bukkit.chatter.Chatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Lang {
    public static String COMMAND_NO_PERMISSION = "&4You do not have permission for that command!";
    public static String PLAYER_COMMAND = "&4This command is only available to players!";

    public static String PLAYER_NOT_FOUND = "&4Player not found!";
    public static String PLAYER_EXEMPT = "&4Player is exempt!";

    public static String CHAT_FORMAT = "&r{prefix}&r&7{sender}&r{suffix}&r&e:&r&7&o {message}";

    public static String BROADCAST_FORMAT = "&2[&dBroadcast&2]&7 {message}";
    public static String BROADCAST_FORMAT_DISCORD = "**[Broadcast]** *{message}*";

    public static String ME_FORMAT = "&6&o* {sender} {message}";
    public static String ME_FORMAT_DISCORD = "***{sender}*** *{message}*";

    public static String TARGET_MUTED = "&7{target}&d muted";
    public static String TARGET_UNMUTED = "&7{target}&d unmuted";
    public static String YOU_ARE_MUTED = "&4You are muted";

    public static String NICK_SET_SELF = "&dYour nickname is now &7{nick}";
    public static String NICK_SET_OTHER = "&dYou set &7{player}&d's nickname to &7{nick}";
    public static String NICK_SET_BY_OTHER = "&dYour nickname was set to &7{nick} &dby &7{player}";
    public static String NICK_REMOVED_SELF = "&dYour nickname has been removed";
    public static String NICK_REMOVED_OTHER = "&dYou removed &7{player}&d's nickname";
    public static String NICK_REMOVED_BY_OTHER = "&dYour nickname was removed by &7{player}";

    public static String SPY_MODE_TOGGLED = "Spy mode toggled &7{toggle}";
    public static String SPY_MODE_TOGGLED_OTHER = "Spy mode toggled &7{toggle}&d for &7{target}";
    public static String SPY_MODE_PREFIX = "&e[&6Spy&e]";

    public static String TELL_SENDER_FORMAT = "&e[&7Me &d-> &7{target}&e] &7{message}";
    public static String TELL_TARGET_FORMAT = "&e[&7Me &d<- &7{sender}&e] &7{message}";
    public static String TELL_SPY_FORMAT = "&e[&7{sender} &d-> &7{target}&e] &7{message}";
    public static String REPLY_NO_TARGET = "&4No one to reply to!";

    private static void init() {
        COMMAND_NO_PERMISSION = getString("command-no-permission", COMMAND_NO_PERMISSION);
        PLAYER_COMMAND = getString("player-command", PLAYER_COMMAND);

        PLAYER_NOT_FOUND = getString("player-not-found", PLAYER_NOT_FOUND);
        PLAYER_EXEMPT = getString("player-exempt", PLAYER_EXEMPT);

        CHAT_FORMAT = getString("chat-format", CHAT_FORMAT);

        BROADCAST_FORMAT = getString("broadcast-format", BROADCAST_FORMAT);
        BROADCAST_FORMAT_DISCORD = getString("broadcast-format-discord", BROADCAST_FORMAT_DISCORD);

        ME_FORMAT = getString("me-format", ME_FORMAT);
        ME_FORMAT_DISCORD = getString("me-format-discord", ME_FORMAT_DISCORD);

        TARGET_MUTED = getString("target-muted", TARGET_MUTED);
        TARGET_UNMUTED = getString("target-unmuted", TARGET_UNMUTED);
        YOU_ARE_MUTED = getString("you-are-muted", YOU_ARE_MUTED);

        NICK_SET_SELF = getString("nick-set-self", NICK_SET_SELF);
        NICK_SET_OTHER = getString("nick-set-other", NICK_SET_OTHER);
        NICK_SET_BY_OTHER = getString("nick-set-by-other", NICK_SET_BY_OTHER);
        NICK_REMOVED_SELF = getString("nick-removed-self", NICK_REMOVED_SELF);
        NICK_REMOVED_OTHER = getString("nick-removed-other", NICK_REMOVED_OTHER);
        NICK_REMOVED_BY_OTHER = getString("nick-removed-by-other", NICK_REMOVED_BY_OTHER);

        SPY_MODE_TOGGLED = getString("spy-mode-toggled", SPY_MODE_TOGGLED);
        SPY_MODE_TOGGLED_OTHER = getString("spy-mode-toggled-other", SPY_MODE_TOGGLED_OTHER);
        SPY_MODE_PREFIX = getString("spy-mode-prefix", SPY_MODE_PREFIX);

        TELL_SENDER_FORMAT = getString("tell-sender", TELL_SENDER_FORMAT);
        TELL_TARGET_FORMAT = getString("tell-target", TELL_TARGET_FORMAT);
        TELL_SPY_FORMAT = getString("tell-spy", TELL_SPY_FORMAT);
        REPLY_NO_TARGET = getString("reply-no-target", REPLY_NO_TARGET);
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the language file
     */
    public static void reload() {
        Chatter plugin = Chatter.getInstance();
        File configFile = new File(plugin.getDataFolder(), Config.LANGUAGE_FILE);
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load " + Config.LANGUAGE_FILE + ", please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the main language file for Chatter.");
        config.options().copyDefaults(true);

        Lang.init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static YamlConfiguration config;

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    /**
     * Sends a message to a recipient
     *
     * @param recipient Recipient of message
     * @param message   Message to send
     */
    public static void send(CommandSender recipient, String message) {
        if (recipient != null) {
            for (String part : colorize(message).split("\n")) {
                recipient.sendMessage(part);
            }
        }
    }

    /**
     * Broadcast a message to server
     *
     * @param message Message to broadcast
     */
    public static void broadcast(String message) {
        for (String part : colorize(message).split("\n")) {
            Bukkit.getOnlinePlayers().forEach(recipient -> recipient.sendMessage(part));
            Bukkit.getConsoleSender().sendMessage(part);
        }
    }

    /**
     * Colorize a String
     *
     * @param str String to colorize
     * @return Colorized String
     */
    public static String colorize(String str) {
        if (str == null) {
            return "";
        }
        str = ChatColor.translateAlternateColorCodes('&', str);
        if (ChatColor.stripColor(str).isEmpty()) {
            return "";
        }
        return str;
    }
}
