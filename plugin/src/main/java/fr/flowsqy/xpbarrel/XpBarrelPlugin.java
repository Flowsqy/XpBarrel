package fr.flowsqy.xpbarrel;

import org.bukkit.plugin.java.JavaPlugin;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.listener.BreakListener;
import fr.flowsqy.xpbarrel.listener.InteractListener;
import fr.flowsqy.xpbarrel.listener.ProtectListener;

public class XpBarrelPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        // TODO Init barrel manager
        final var barrelManager = new BarrelManager(null);
        final var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new BreakListener(barrelManager), this);
        pluginManager.registerEvents(new ProtectListener(barrelManager), this);
        pluginManager.registerEvents(new InteractListener(barrelManager), this);
    }

}
