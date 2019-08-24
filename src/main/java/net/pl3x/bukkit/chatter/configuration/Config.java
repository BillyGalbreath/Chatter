package net.pl3x.bukkit.chatter.configuration;

import com.google.common.base.Throwables;
import net.pl3x.bukkit.chatter.Chatter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class Config {
    public static String LANGUAGE_FILE = "lang-en.yml";

    public static boolean INHERIT_PREFIXES_FROM_ALL_GROUPS = true;
    public static boolean INHERIT_SUFFIXES_FROM_ALL_GROUPS = true;

    public static List<String> RACISM = new ArrayList<>();
    public static String AUTO_BAN_RACISM = "ban {player} AutoBan: Racism";

    public static int ME_COOLDOWN = 300; // 5 minutes

    private static void init() {
        LANGUAGE_FILE = getString("language-file", LANGUAGE_FILE);

        INHERIT_PREFIXES_FROM_ALL_GROUPS = getBoolean("inherit-prefixes-from-all-groups", INHERIT_PREFIXES_FROM_ALL_GROUPS);
        INHERIT_SUFFIXES_FROM_ALL_GROUPS = getBoolean("inherit-suffixes-from-all-groups", INHERIT_SUFFIXES_FROM_ALL_GROUPS);

        RACISM.clear();
        RACISM.addAll(getList("racist-words", Arrays.asList("nigger", "nigga")));
        AUTO_BAN_RACISM = getString("auto-ban-racism", AUTO_BAN_RACISM);

        ME_COOLDOWN = getInt("cooldown.me", ME_COOLDOWN);
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the configuration file
     */
    public static void reload() {
        Chatter plugin = Chatter.getInstance();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load config.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the configuration file for Chatter.");
        config.options().copyDefaults(true);

        Config.init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static YamlConfiguration config;

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(String path, T def) {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }
}
