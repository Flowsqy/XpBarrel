package fr.flowsqy.xpbarrel.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.barrel.BlockPosition;
import fr.flowsqy.xpbarrel.barrel.ItemManager;
import fr.flowsqy.xpbarrel.barrel.XpBarrelSnapshot;

public class PlaceListener implements Listener {

    private final BarrelManager barrelManager;
    private final ItemManager itemManager;

    public PlaceListener(@NotNull BarrelManager barrelManager, @NotNull ItemManager itemManager) {
        this.barrelManager = barrelManager;
        this.itemManager = itemManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlace(BlockPlaceEvent event) {
        final var usedItem = event.getItemInHand();
        final var extractedData = itemManager.extractData(usedItem);
        if (extractedData == null) {
            return;
        }
        final var owner = extractedData.owner();
        final var placerPlayer = event.getPlayer();
        if (owner != null && !owner.equals(placerPlayer.getUniqueId())
                && !placerPlayer.hasPermission("xpbarrel.place-other")) {
            event.setCancelled(true);
            return;
        }
        final var placedBlock = event.getBlockPlaced();
        final var position = BlockPosition.from(placedBlock.getLocation());
        final var world = placedBlock.getWorld().getName();
        final var ownerId = owner == null ? placerPlayer.getUniqueId() : owner;
        final var xpBarrel = new XpBarrelSnapshot(extractedData.experience(), ownerId);
        barrelManager.addBarrelAt(world, position, xpBarrel);
    }

}
