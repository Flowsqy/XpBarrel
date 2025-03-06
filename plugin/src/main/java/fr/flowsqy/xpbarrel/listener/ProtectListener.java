package fr.flowsqy.xpbarrel.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.barrel.BlockPosition;

public class ProtectListener implements Listener {

    private final BarrelManager barrelManager;

    public ProtectListener(@NotNull BarrelManager barrelManager) {
        this.barrelManager = barrelManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onExplode(BlockExplodeEvent event) {
        final var blockState = event.getExplodedBlockState();
        if (blockState.getType() != Material.BARREL) {
            return;
        }
        final var position = BlockPosition.from(blockState.getLocation());
        final var xpBarrel = barrelManager.getBarrelAt(blockState.getWorld().getName(), position);
        if (xpBarrel == null) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBurn(BlockBurnEvent event) {
        final var block = event.getBlock();
        if (block.getType() != Material.BARREL) {
            return;
        }
        final var position = BlockPosition.from(block.getLocation());
        final var xpBarrel = barrelManager.getBarrelAt(block.getWorld().getName(), position);
        if (xpBarrel == null) {
            return;
        }
        event.setCancelled(true);
    }

}
