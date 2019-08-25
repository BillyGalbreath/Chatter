package net.pl3x.bukkit.chatter;

import net.pl3x.bukkit.chatter.command.CmdBroadcast;
import net.pl3x.bukkit.chatter.command.CmdChatter;
import net.pl3x.bukkit.chatter.command.CmdFlip;
import net.pl3x.bukkit.chatter.command.CmdMe;
import net.pl3x.bukkit.chatter.command.CmdMute;
import net.pl3x.bukkit.chatter.command.CmdNick;
import net.pl3x.bukkit.chatter.command.CmdReply;
import net.pl3x.bukkit.chatter.command.CmdSpy;
import net.pl3x.bukkit.chatter.command.CmdTell;
import net.pl3x.bukkit.chatter.configuration.Config;
import net.pl3x.bukkit.chatter.configuration.Lang;
import net.pl3x.bukkit.chatter.listener.ChatListener;
import net.pl3x.bukkit.chatter.listener.JoinListener;
import net.pl3x.bukkit.chatter.listener.RacismListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Chatter extends JavaPlugin {
    private static Chatter instance;

    public Chatter() {
        instance = this;
    }

    public void onEnable() {
        Config.reload();
        Lang.reload();

        if (!Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            getLogger().severe("Missing required dependency: LuckPerms");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new RacismListener(), this);

        getCommand("chatter").setExecutor(new CmdChatter(this));
        getCommand("broadcast").setExecutor(new CmdBroadcast(this));
        getCommand("flip").setExecutor(new CmdFlip());
        getCommand("me").setExecutor(new CmdMe(this));
        getCommand("mute").setExecutor(new CmdMute());
        getCommand("nick").setExecutor(new CmdNick());
        getCommand("reply").setExecutor(new CmdReply());
        getCommand("spy").setExecutor(new CmdSpy());
        getCommand("tell").setExecutor(new CmdTell());
    }

    public static Chatter getInstance() {
        return instance;
    }
}
