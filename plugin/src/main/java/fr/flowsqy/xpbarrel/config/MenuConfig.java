package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.xpbarrel.menu.MainMenuRegisterHandler;

public class MenuConfig {

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
    public EventInventory getMainMenu(@NotNull MenuFactory menuFactory) {
        final var inventorySection = configuration.getConfigurationSection("main-menu");
        if (inventorySection == null) {
            return new EventInventory(menuFactory, "", 3);
        }
        return EventInventory.deserialize(inventorySection, menuFactory, new MainMenuRegisterHandler(inventorySection));
    }

}
