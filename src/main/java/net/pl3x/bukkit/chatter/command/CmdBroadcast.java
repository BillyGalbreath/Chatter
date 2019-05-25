package net.pl3x.bukkit.chatter.command;

import net.pl3x.bukkit.chatter.Chatter;
import net.pl3x.bukkit.chatter.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CmdBroadcast implements TabExecutor {
    private final Chatter plugin;

    public CmdBroadcast(Chatter plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        String arg = args[args.length - 1].toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getName().toLowerCase().startsWith(arg))
                .map(HumanEntity::getName)
                .collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.broadcast")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            return false; // show command usage
        }

        Lang.broadcast(Lang.BROADCAST_FORMAT
                .replace("{message}", String.join(" ", args)));

        if (plugin.getDiscordHook() != null) {
            plugin.getDiscordHook().sendToDiscord(Lang.BROADCAST_FORMAT_DISCORD
                    .replace("{message}", String.join(" ", args)));
        }

        return true;
    }
}
