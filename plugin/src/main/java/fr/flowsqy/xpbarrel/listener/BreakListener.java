package fr.flowsqy.xpbarrel.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;

public class BreakListener implements Listener {

    private final BarrelManager barrelManager;

    public BreakListener(@NotNull BarrelManager barrelManager) {
        this.barrelManager = barrelManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onExplode(BlockExplodeEvent event) {
        final var blockState = event.getExplodedBlockState();
        if (blockState.getType() != Material.BARREL) {
            return;
        }
        barrelManager.getBarrelAt();
        // TODO Protect barrel
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBurn(BlockBurnEvent event) {
        final var block = event.getIgnitingBlock();
        if (block.getType() != Material.BARREL) {
            return;
        }
        barrelManager.getBarrelAt();
        // TODO Protect barrel
    }

}
