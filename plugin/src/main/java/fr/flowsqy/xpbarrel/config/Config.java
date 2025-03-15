package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.util.Objects;

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

}
