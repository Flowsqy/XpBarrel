package fr.flowsqy.xpbarrel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.config.ConfigLoader;
import fr.flowsqy.xpbarrel.listener.BreakListener;
import fr.flowsqy.xpbarrel.listener.InteractListener;
import fr.flowsqy.xpbarrel.listener.ProtectListener;

public class XpBarrelPlugin extends JavaPlugin {

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
        // TODO Init barrel manager
        final var barrelManager = new BarrelManager(null);
        final var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BreakListener(barrelManager), this);
        pluginManager.registerEvents(new ProtectListener(barrelManager), this);
        pluginManager.registerEvents(new InteractListener(barrelManager), this);
    }

}
