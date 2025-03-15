package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Config {

    private YamlConfiguration configuration;

    public void load(@NotNull ConfigLoader configLoader, @NotNull JavaPlugin plugin, @NotNull String fileName) {
        final File configFile = configLoader.initFile(plugin.getDataFolder(),
                Objects.requireNonNull(plugin.getResource(fileName)), fileName);
        try {
            configuration = YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public String getBarrelName() {
        final var rawName = configuration.getString("item.name");
        if (rawName == null) {
            return ChatColor.RESET + "XpBarrel";
        }
        return ChatColor.translateAlternateColorCodes('&', rawName);
    }

    @NotNull
    public String[] getBarrelLore() {
        final var rawLore = configuration.getStringList("item.lore");
        final var lore = new String[rawLore.size()];
        final var iterator = rawLore.iterator();
        for (int i = 0; i < lore.length; i++) {
            lore[i] = ChatColor.translateAlternateColorCodes('&', iterator.next());
        }
        return lore;
    }

}
