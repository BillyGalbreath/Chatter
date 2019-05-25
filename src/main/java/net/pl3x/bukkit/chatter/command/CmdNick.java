package net.pl3x.bukkit.chatter.command;

import net.pl3x.bukkit.chatter.configuration.Lang;
import net.pl3x.bukkit.chatter.configuration.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CmdNick implements TabExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("command.nick.other")) {
            String arg = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> !player.hasPermission("command.nick.exempt"))
                    .filter(player -> player.getName().toLowerCase().startsWith(arg))
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.nick")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length < 1) {
            return false; // show command usage
        }

        String nick;
        Player target;

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                Lang.send(sender, Lang.PLAYER_COMMAND);
                return true;
            }
            nick = args[0].trim();
            target = (Player) sender;
        } else {
            nick = args[1];
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Lang.send(sender, Lang.PLAYER_NOT_FOUND);
                return true;
            }
        }

        if (target != sender) {
            if (!sender.hasPermission("command.nick.other")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            if (target.hasPermission("command.nick.exempt")) {
                Lang.send(sender, Lang.PLAYER_EXEMPT);
                return true;
            }
        }

        PlayerConfig.getConfig(target).thenAccept(config -> {
            if (nick.equalsIgnoreCase("remove")) {
                config.setNick(null);
                if (target == sender) {
                    Lang.send(sender, Lang.NICK_REMOVED_SELF);
                } else {
                    Lang.send(sender, Lang.NICK_REMOVED_OTHER
                            .replace("{player}", target.getName()));
                    Lang.send(target.getPlayer(), Lang.NICK_REMOVED_BY_OTHER
                            .replace("{player}", sender.getName()));
                }
            } else {
                config.setNick(nick);
                if (target == sender) {
                    Lang.send(sender, Lang.NICK_SET_SELF
                            .replace("{nick}", nick));
                } else {
                    Lang.send(sender, Lang.NICK_SET_OTHER
                            .replace("{player}", target.getName())
                            .replace("{nick}", nick));
                    Lang.send(target.getPlayer(), Lang.NICK_SET_BY_OTHER
                            .replace("{player}", sender.getName())
                            .replace("{nick}", nick));
                }
            }
            target.setDisplayName(config.getNick());
        });
        return true;
    }
}
