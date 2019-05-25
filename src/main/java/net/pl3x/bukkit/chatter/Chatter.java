package net.pl3x.bukkit.chatter;

import net.pl3x.bukkit.chatter.command.CmdBroadcast;
import net.pl3x.bukkit.chatter.command.CmdFlip;
import net.pl3x.bukkit.chatter.command.CmdMe;
import net.pl3x.bukkit.chatter.command.CmdMute;
import net.pl3x.bukkit.chatter.command.CmdNick;
import net.pl3x.bukkit.chatter.command.CmdReply;
import net.pl3x.bukkit.chatter.command.CmdSpy;
import net.pl3x.bukkit.chatter.command.CmdTell;
import net.pl3x.bukkit.chatter.configuration.Config;
import net.pl3x.bukkit.chatter.configuration.Lang;
import net.pl3x.bukkit.chatter.hook.DiscordHook;
import net.pl3x.bukkit.chatter.hook.Vault;
import net.pl3x.bukkit.chatter.listener.ChatListener;
import net.pl3x.bukkit.chatter.listener.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Chatter extends JavaPlugin {
    private static Chatter instance;

    private DiscordHook discordHook;

    public Chatter() {
        instance = this;
    }

    public void onEnable() {
        Config.reload();
        Lang.reload();

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            getLogger().severe("Missing required dependency: Vault");
            return;
        }

        if (!Vault.setupPermissions()) {
            getLogger().severe("Vault could not find a permissions plugin to hook to!");
            return;
        }

        if (!Vault.setupChat()) {
            getLogger().severe("Vault could not register chat service! Do you have a permissions plugin installed?");
            return;
        }

        discordHook = new DiscordHook(getServer().getPluginManager().getPlugin("Discord4Bukkit"));

        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);

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

    public DiscordHook getDiscordHook() {
        return discordHook;
    }
}
