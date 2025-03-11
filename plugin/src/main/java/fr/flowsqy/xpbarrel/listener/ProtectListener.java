package fr.flowsqy.xpbarrel.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.barrel.BlockPosition;

public class ProtectListener implements Listener {

    private final BarrelManager barrelManager;

    public ProtectListener(@NotNull BarrelManager barrelManager) {
        this.barrelManager = barrelManager;
    }

    private boolean isBarrel(@NotNull Block block) {
        if (block.getType() != Material.BARREL) {
            return false;
        }
        final var position = BlockPosition.from(block.getLocation());
        final var xpBarrel = barrelManager.getBarrelAt(block.getWorld().getName(), position);
        return xpBarrel != null;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(this::isBarrel);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(this::isBarrel);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        if (isBarrel(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemTransfer(InventoryMoveItemEvent event) {
        final var destinationInventory = event.getDestination();
        if (destinationInventory.getType() != InventoryType.BARREL) {
            return;
        }
        final var location = destinationInventory.getLocation();
        if (location == null) {
            return;
        }
        final var world = location.getWorld();
        if (world == null) {
            return;
        }
        final var position = BlockPosition.from(location);
        final var xpBarrel = barrelManager.getBarrelAt(world.getName(), position);
        if (xpBarrel == null) {
            return;
        }
        event.setCancelled(true);
    }

}
