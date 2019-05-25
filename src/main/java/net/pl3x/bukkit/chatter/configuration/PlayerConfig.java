package net.pl3x.bukkit.chatter.configuration;

import net.pl3x.bukkit.chatter.Chatter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PlayerConfig extends YamlConfiguration {
    private static final Map<UUID, PlayerConfig> configs = new HashMap<>();

    public static CompletableFuture<PlayerConfig> getConfig(OfflinePlayer player) {
        return getConfig(player.getUniqueId());
    }

    public static CompletableFuture<PlayerConfig> getConfig(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            synchronized (configs) {
                return configs.computeIfAbsent(uuid, k -> new PlayerConfig(uuid));
            }
        });
    }

    public static Collection<PlayerConfig> getSpies() {
        synchronized (configs) {
            return configs.values().stream()
                    .filter(PlayerConfig::isSpying)
                    .collect(Collectors.toSet());
        }
    }

    private final File file;
    private final Object saveLock = new Object();
    private final UUID uuid;

    private PlayerConfig(UUID uuid) {
        super();
        File dir = new File(Chatter.getInstance().getDataFolder(), "userdata");
        this.file = new File(dir, uuid + ".yml");
        this.uuid = uuid;
        load();
    }

    public boolean isUUID(OfflinePlayer player) {
        return isUUID(player.getUniqueId());
    }

    public boolean isUUID(UUID uuid) {
        return this.uuid == uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    private void load() {
        synchronized (saveLock) {
            try {
                load(file);
            } catch (Exception ignore) {
            }
        }
    }

    private void save() {
        synchronized (saveLock) {
            try {
                save(file);
            } catch (Exception ignore) {
            }
        }
    }

    public String getNick() {
        return getString("nickname");
    }

    public void setNick(String nickname) {
        set("nickname", nickname);
    }

    public boolean isMuted() {
        return getBoolean("muted", false);
    }

    public void setMuted(boolean isMuted) {
        set("muted", isMuted);
        save();
    }

    public boolean isSpying() {
        return getBoolean("spying", false);
    }

    public void setSpying(boolean isSpying) {
        set("spying", isSpying);
        save();
    }
}
