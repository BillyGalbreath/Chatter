package net.pl3x.bukkit.chatter.command;

import net.pl3x.bukkit.chatter.Chatter;
import net.pl3x.bukkit.chatter.configuration.Config;
import net.pl3x.bukkit.chatter.configuration.Lang;
import net.pl3x.bukkit.chatter.configuration.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdMe implements TabExecutor {
    private final Chatter plugin;

    public CmdMe(Chatter plugin) {
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
        if (!sender.hasPermission("command.me")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            return false; // show usage
        }

        if (sender instanceof Player) {
            PlayerConfig.getConfig((Player) sender).thenAccept(config -> {
                if (config.isMuted()) {
                    Lang.send(sender, Lang.YOU_ARE_MUTED);
                } else {
                    broadcast(sender, command, args);
                }
            });
        } else {
            broadcast(sender, command, args);
        }
        return true;
    }

    private void broadcast(CommandSender sender, Command command, String[] args) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Lang.ME_FORMAT
                .replace("{sender}", sender.getName().equals("CONSOLE") ? "Console" : sender.getName())
                .replace("{message}", String.join(" ", args))));

        if (sender instanceof Player && Bukkit.getPluginManager().isPluginEnabled("CmdCD")) {
            net.pl3x.bukkit.cmdcd.CmdCD.addCooldown(command, ((Player) sender).getUniqueId(), Config.ME_COOLDOWN);
        }
    }
}
