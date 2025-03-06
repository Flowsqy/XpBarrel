package fr.flowsqy.xpbarrel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.config.BarrelStorageLoader;
import fr.flowsqy.xpbarrel.config.BarrelStorageSaver;
import fr.flowsqy.xpbarrel.config.ConfigLoader;
import fr.flowsqy.xpbarrel.listener.BreakListener;
import fr.flowsqy.xpbarrel.listener.InteractListener;
import fr.flowsqy.xpbarrel.listener.ProtectListener;

public class XpBarrelPlugin extends JavaPlugin {

    private BarrelManager barrelManager;

    public XpBarrelPlugin() {
        barrelManager = null;
    }

    @Override
    public void onEnable() {
        final var configLoader = new ConfigLoader();
        final var dataFolder = getDataFolder();
        final var logger = getLogger();
        if (!configLoader.checkDataFolder(dataFolder)) {
            Bukkit.getPluginManager().disablePlugin(this);
            logger.warning("Can't write in the plugin directory. Disable the plugin");
            return;
        }
        final var barrelStorage = new BarrelStorageLoader();
        barrelStorage.load(dataFolder, logger);
        final var loadedBarrels = barrelStorage.loadBarrels(logger);
        barrelManager = new BarrelManager(loadedBarrels);
        final var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BreakListener(barrelManager), this);
        pluginManager.registerEvents(new ProtectListener(barrelManager), this);
        pluginManager.registerEvents(new InteractListener(barrelManager), this);
    }

    @Override
    public void onDisable() {
        final var configLoader = new ConfigLoader();
        final var dataFolder = getDataFolder();
        if (!configLoader.checkDataFolder(dataFolder)) {
            return;
        }
        if (barrelManager == null) {
            return;
        }
        final var logger = getLogger();
        final var barrelStorageSaver = new BarrelStorageSaver();
        barrelStorageSaver.saveBarrels(logger, dataFolder, barrelManager.getSnapshot());
    }

}
