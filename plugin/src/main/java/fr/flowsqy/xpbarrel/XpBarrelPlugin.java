package fr.flowsqy.xpbarrel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.barrel.ItemManager;
import fr.flowsqy.xpbarrel.config.ConfigLoader;
import fr.flowsqy.xpbarrel.config.MessagesContainer;
import fr.flowsqy.xpbarrel.listener.BreakListener;
import fr.flowsqy.xpbarrel.listener.InteractListener;
import fr.flowsqy.xpbarrel.listener.PlaceListener;
import fr.flowsqy.xpbarrel.listener.ProtectListener;
import fr.flowsqy.xpbarrel.load.PluginData;
import fr.flowsqy.xpbarrel.load.PluginDataLoader;
import fr.flowsqy.xpbarrel.menu.MenuManager;

public class XpBarrelPlugin extends JavaPlugin {

    private PluginData pluginData;

    public XpBarrelPlugin() {
        pluginData = null;
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
        final var pluginDataLoader = new PluginDataLoader();
        final var barrelManager = new BarrelManager();
        final var itemManager = new ItemManager(this);
        final var menuManager = new MenuManager(new MenuFactory(this));
        final var breakMC = new BreakListener.BreakMessagesContainer();
        final var interactMC = new InteractListener.InteractMessagesContainer();
        pluginData = new PluginData(this, barrelManager, itemManager, menuManager,
                new MessagesContainer[] { breakMC, interactMC });
        pluginDataLoader.load(pluginData);
        final var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BreakListener(barrelManager, itemManager, breakMC), this);
        pluginManager.registerEvents(new ProtectListener(barrelManager), this);
        pluginManager.registerEvents(new InteractListener(this, barrelManager, menuManager, interactMC), this);
        pluginManager.registerEvents(new PlaceListener(barrelManager, itemManager), this);
    }

    @Override
    public void onDisable() {
        final var configLoader = new ConfigLoader();
        final var dataFolder = getDataFolder();
        if (!configLoader.checkDataFolder(dataFolder)) {
            return;
        }
        final var pluginLoader = new PluginDataLoader();
        pluginLoader.save(pluginData);
    }

}
