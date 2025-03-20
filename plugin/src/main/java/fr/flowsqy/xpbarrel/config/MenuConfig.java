package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.util.Objects;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.conversation.ConversationBuilder;
import fr.flowsqy.xpbarrel.menu.MainMenuRegisterHandler;
import fr.flowsqy.xpbarrel.menu.MenuManager;

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
    public EventInventory getMainMenu(@NotNull XpBarrelPlugin plugin, @NotNull MenuManager menuManager,
            @NotNull Config config, @NotNull MessageConfig messageConfig,
            @NotNull ConversationBuilder conversationBuilder) {
        final var inventorySection = configuration.getConfigurationSection("main-menu");
        if (inventorySection == null) {
            return new EventInventory(menuManager.getMenuFactory(), "", 3);
        }
        return EventInventory.deserialize(inventorySection, menuManager.getMenuFactory(),
                new MainMenuRegisterHandler(plugin, menuManager, inventorySection, config.getMaxExperience(),
                        messageConfig, conversationBuilder));
    }

}
