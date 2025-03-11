package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageConfig {

    private YamlConfiguration configuration;
    private String prefix;

    public void load(@NotNull ConfigLoader configLoader, @NotNull JavaPlugin plugin, @NotNull String fileName) {
        final File configFile = configLoader.initFile(plugin.getDataFolder(),
                Objects.requireNonNull(plugin.getResource(fileName)), fileName);
        try {
            configuration = YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    private String formatColor(@Nullable String message) {
        return message == null ? null : ChatColor.translateAlternateColorCodes('&', message);
    }

    public void loadPrefix() {
        prefix = formatColor(configuration.getString("prefix"));
    }

    @Nullable
    public String getMessage(@NotNull String path) {
        final String message = formatColor(configuration.getString(path));
        if (message == null) {
            return null;
        }
        return prefix == null ? message : message.replace("%prefix%", prefix);
    }

    @Nullable
    public BaseComponent getComponentMessage(@NotNull String path) {
        final String message = getMessage(path);
        if (message == null) {
            return null;
        }
        return TextComponent.fromLegacy(message);
    }

}
