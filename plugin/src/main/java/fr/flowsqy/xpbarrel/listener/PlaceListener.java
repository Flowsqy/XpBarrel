package fr.flowsqy.xpbarrel.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.ItemManager;

public class PlaceListener implements Listener {

    private final ItemManager itemManager;

    public PlaceListener(@NotNull ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlace(BlockPlaceEvent event) {
        final var usedItem = event.getItemInHand();
        final var extractedData = itemManager.extractData(usedItem);
        if (extractedData == null) {
            return;
        }
        event.setCancelled(true);
    }

}
