package fr.flowsqy.xpbarrel.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.barrel.BlockPosition;
import fr.flowsqy.xpbarrel.barrel.ItemManager;

public class BreakListener implements Listener {

    private final BarrelManager barrelManager;
    private final ItemManager itemManager;

    public BreakListener(@NotNull BarrelManager barrelManager, @NotNull ItemManager itemManager) {
        this.barrelManager = barrelManager;
        this.itemManager = itemManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBreak(BlockBreakEvent event) {
        final var brokenBlock = event.getBlock();
        if (brokenBlock.getType() != Material.BARREL) {
            return;
        }
        final var location = brokenBlock.getLocation();
        final var position = BlockPosition.from(location);
        final var world = brokenBlock.getWorld();
        final var worldName = brokenBlock.getWorld().getName();
        final var xpBarrel = barrelManager.getBarrelAt(worldName, position);
        if (xpBarrel == null) {
            return;
        }
        final var breakerPlayer = event.getPlayer();
        if (!xpBarrel.owner().equals(breakerPlayer.getUniqueId())
                && !breakerPlayer.hasPermission("xpbarrel.break-other")) {
            event.setCancelled(false);
            return;
        }
        event.setDropItems(false);
        final var dropItem = itemManager.generateItem(xpBarrel.owner(), xpBarrel.experience());
        if (dropItem != null) {
            world.dropItemNaturally(location, dropItem);
        }
    }

}
