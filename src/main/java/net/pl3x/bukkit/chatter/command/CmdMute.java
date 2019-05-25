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

public class CmdMute implements TabExecutor {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && sender.hasPermission("command.mute")) {
            String arg = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player -> !player.hasPermission("command.mute.exempt"))
                    .filter(player -> player.getName().toLowerCase().startsWith(arg))
                    .map(HumanEntity::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.mute")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length == 0) {
            return false; // show command usage
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Lang.send(sender, Lang.PLAYER_NOT_FOUND);
            return true;
        }

        PlayerConfig.getConfig(target).thenAccept(config -> {
            boolean mute = !config.isMuted();

            if (mute && target.hasPermission("command.mute.exempt")) {
                Lang.send(sender, Lang.PLAYER_EXEMPT);
                return;
            }

            config.setMuted(mute);

            Lang.send(sender, (mute ? Lang.TARGET_MUTED : Lang.TARGET_UNMUTED)
                    .replace("{target}", target.getName()));
        });
        return true;
    }
}
