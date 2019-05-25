package net.pl3x.bukkit.chatter.hook;

import org.bukkit.plugin.Plugin;

public class DiscordHook {
    private net.pl3x.bukkit.discord4bukkit.bot.Bot bot;

    public DiscordHook(Plugin plugin) {
        if (plugin instanceof net.pl3x.bukkit.discord4bukkit.D4BPlugin) {
            bot = ((net.pl3x.bukkit.discord4bukkit.D4BPlugin) plugin).getBot();
        }
    }

    public void sendToDiscord(String message) {
        if (bot != null) {
            bot.sendMessageToDiscord(message);
        }
    }
}
