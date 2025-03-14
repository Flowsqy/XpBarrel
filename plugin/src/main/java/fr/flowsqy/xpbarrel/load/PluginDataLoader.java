package fr.flowsqy.xpbarrel.load;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.command.CommandLoader;
import fr.flowsqy.xpbarrel.config.BarrelStorageLoader;
import fr.flowsqy.xpbarrel.config.BarrelStorageSaver;
import fr.flowsqy.xpbarrel.config.ConfigLoader;
import fr.flowsqy.xpbarrel.config.MessageConfig;

public class PluginDataLoader {

    public void load(@NotNull PluginData pluginData) {
        final var configLoader = new ConfigLoader();
        final var plugin = pluginData.plugin();
        final var logger = plugin.getLogger();
        final var messageConfig = new MessageConfig();
        messageConfig.load(configLoader, plugin, "messages.yml");
        messageConfig.loadPrefix();
        final var barrelStorage = new BarrelStorageLoader();
        barrelStorage.load(plugin.getDataFolder(), logger);
        final var loadedBarrels = barrelStorage.loadBarrels(logger);
        pluginData.barrelManager().load(loadedBarrels);
        final var commandLoader = new CommandLoader();
        commandLoader.load(pluginData, messageConfig);
    }

    public void save(@NotNull XpBarrelPlugin plugin, @NotNull BarrelManager barrelManager) {
        final var logger = plugin.getLogger();
        final var dataFolder = plugin.getDataFolder();
        final var barrelStorageSaver = new BarrelStorageSaver();
        barrelStorageSaver.load(dataFolder, logger);
        barrelStorageSaver.saveBarrels(logger, dataFolder, barrelManager.getSnapshot());
    }

}
