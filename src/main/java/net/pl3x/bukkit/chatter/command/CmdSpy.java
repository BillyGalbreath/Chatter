package net.pl3x.bukkit.chatter.command;

import net.pl3x.bukkit.chatter.configuration.Lang;
import net.pl3x.bukkit.chatter.configuration.PlayerConfig;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdSpy implements TabExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("command.spy.others")) {
            String arg = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getName().toLowerCase().startsWith(arg))
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.spy")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Lang.send(sender, Lang.PLAYER_NOT_FOUND);
                return true;
            }
        }

        if (target != sender) {
            if (!sender.hasPermission("command.spy.other")) {
                Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
                return true;
            }

            if (target.hasPermission("command.spy.exempt")) {
                Lang.send(sender, Lang.PLAYER_EXEMPT);
                return true;
            }
        }

        PlayerConfig.getConfig(target).thenAccept(config -> {
            boolean spying = !config.isSpying();

            config.setSpying(spying);

            if (target == sender) {
                Lang.send(sender, Lang.SPY_MODE_TOGGLED
                        .replace("{toggle}", BooleanUtils.toStringOnOff(spying)));
            } else {
                Lang.send(sender, Lang.SPY_MODE_TOGGLED_OTHER
                        .replace("{toggle}", BooleanUtils.toStringOnOff(spying))
                        .replace("{target}", target.getName()));
            }
        });
        return true;
    }
}
