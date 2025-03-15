package fr.flowsqy.xpbarrel.load;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.barrel.ItemManager;
import fr.flowsqy.xpbarrel.menu.MenuManager;

public record PluginData(@NotNull XpBarrelPlugin plugin, @NotNull BarrelManager barrelManager,
        @NotNull ItemManager itemManager, @NotNull MenuManager menuManager) {
}
