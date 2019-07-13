package net.pl3x.bukkit.chatter.command;

import net.pl3x.bukkit.chatter.Chatter;
import net.pl3x.bukkit.chatter.configuration.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdFlip implements TabExecutor {
    private static final String normal = "abcdefghijklmnopqrstuvwxyz_,;.?!/\\'ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String flipped = "ɐqɔpǝɟbɥıظʞןɯuodbɹsʇnʌʍxʎz‾'؛˙¿¡/\\,∀qϽᗡƎℲƃHIſʞ˥WNOԀὉᴚS⊥∩ΛMXʎZ";

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
        FlipType flipType = FlipType.get(label);

        if (!sender.hasPermission("command." + flipType.permission)) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        if (!(sender instanceof Player)) {
            Lang.send(sender, Lang.PLAYER_COMMAND);
            return true;
        }

        String message = flipType.text;
        if (flipType == FlipType.FLIP && args.length > 0) {
            message = flipText(String.join(" ", args));
        } else if (flipType == FlipType.SHRUG && args.length > 0) {
            message = String.join(" ", args) + " " + flipType.text;
        }

        ((Player) sender).chat(message);
        return true;
    }

    private String flipText(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char letter = str.charAt(i);
            int a = normal.indexOf(letter);
            sb.append((a != -1) ? flipped.charAt(a) : letter);
        }
        return new StringBuilder(sb.toString()).reverse().toString();
    }

    public enum FlipType {
        FLIP("flip", "(╯°□°）╯︵ ┻━┻"),
        RUSSIA("russia", "ノ┬─┬ノ ︵ ( \\o°o)\\"),
        SHRUG("shrug", "¯\\_(ツ)_/¯"),
        UNFLIP("unflip", "┬─┬ ノ( ゜-゜ノ)");

        private final String permission;
        private final String text;

        FlipType(String permission, String text) {
            this.permission = permission;
            this.text = text;
        }

        public static FlipType get(String string) {
            if (string == null) {
                return FLIP;
            }
            String pluginName = Chatter.getInstance().getName().toUpperCase();
            string = string.toUpperCase().replace(pluginName + ":", "");
            return valueOf(string);
        }
    }
}
