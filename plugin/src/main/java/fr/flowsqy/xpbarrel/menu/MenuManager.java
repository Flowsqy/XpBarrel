package fr.flowsqy.xpbarrel.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.abstractmenu.factory.MenuFactory;
import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.barrel.XpBarrel;

public class MenuManager {

    private final MenuFactory menuFactory;
    private final Map<UUID, XpBarrel> playerBarrels;
    private EventInventory mainMenu;

    public MenuManager(@NotNull MenuFactory menuFactory) {
        this.menuFactory = menuFactory;
        playerBarrels = new HashMap<>();
    }

    @NotNull
    public MenuFactory getMenuFactory() {
        return menuFactory;
    }

    public void load(@NotNull EventInventory mainMenu) {
        this.mainMenu = mainMenu;
        mainMenu.setCloseCallback(p -> removeWatcher(p.getUniqueId()));
    }

    public void unload(@NotNull XpBarrelPlugin plugin) {
        for (var id : playerBarrels.keySet()) {
            final var player = Bukkit.getPlayer(id);
            if (player == null) {
                continue;
            }
            player.closeInventory();
        }
    }

    public void addWatcher(@NotNull UUID playerId, @NotNull XpBarrel xpBarrel) {
        playerBarrels.put(playerId, xpBarrel);
        xpBarrel.addWatcher(playerId);
    }

    public void removeWatcher(@NotNull UUID playerId) {
        final var xpBarrel = playerBarrels.remove(playerId);
        if (xpBarrel == null) {
            return;
        }
        xpBarrel.removeWatcher(playerId);
    }

    @Nullable
    public XpBarrel getWatchedBarrel(@NotNull UUID playerId) {
        return playerBarrels.get(playerId);
    }

    public void openMainMenu(@NotNull Player player, @NotNull XpBarrel xpBarrel) {
        mainMenu.open(player, xpBarrel.getWatchingId());
    }

}
