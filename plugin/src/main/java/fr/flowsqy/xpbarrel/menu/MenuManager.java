package fr.flowsqy.xpbarrel.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.inventory.EventInventory;

public class MenuManager {

    private final MenuFactory menuFactory;
    private EventInventory mainMenu;

    public MenuManager(@NotNull MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
    }

    @NotNull
    public MenuFactory getMenuFactory() {
        return menuFactory;
    }

    public void load(@NotNull EventInventory mainMenu) {
        this.mainMenu = mainMenu;
    }

    public void openMainMenu(@NotNull Player player) {
        mainMenu.open(player);
    }

}
