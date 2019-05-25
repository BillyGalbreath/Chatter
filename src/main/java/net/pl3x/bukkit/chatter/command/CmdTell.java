package net.pl3x.bukkit.chatter.command;

import net.pl3x.bukkit.chatter.configuration.Lang;
import net.pl3x.bukkit.chatter.configuration.PlayerConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdTell implements TabExecutor {
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
        if (!(sender instanceof Player)) {
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        if (!sender.hasPermission("command.tell")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (args.length < 2) {
            return false; // show command usage
        }

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Lang.send(sender, Lang.PLAYER_NOT_FOUND);
            return true;
        }

        String message = String.join(" ", Arrays.asList(args).subList(1, args.length));
        if (Lang.colorize(message).isEmpty()) {
            return false; // show command usage
        }

        PlayerConfig.getConfig(player).thenAccept(senderConfig -> {
            if (senderConfig.isMuted()) {
                Lang.send(sender, Lang.YOU_ARE_MUTED);
                return;
            }

            CmdReply.REPLY_DB.put(player, target);
            CmdReply.REPLY_DB.put(target, player);

            Lang.send(target, Lang.TELL_TARGET_FORMAT
                    .replace("{sender}", sender.getName())
                    .replace("{target}", target.getName())
                    .replace("{message}", message));
            Lang.send(sender, Lang.TELL_SENDER_FORMAT
                    .replace("{sender}", sender.getName())
                    .replace("{target}", target.getName())
                    .replace("{message}", message));

            PlayerConfig.getSpies().stream()
                    .filter(spy -> !(spy.isUUID(player) || spy.isUUID(target)))
                    .forEach(spy -> Lang.send(spy.getPlayer(), Lang.SPY_MODE_PREFIX + Lang.TELL_SPY_FORMAT
                            .replace("{sender}", sender.getName())
                            .replace("{target}", target.getName())
                            .replace("{message}", message)));
        });
        return true;
    }
}
