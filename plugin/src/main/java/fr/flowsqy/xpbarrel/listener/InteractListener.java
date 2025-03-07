package fr.flowsqy.xpbarrel.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.barrel.BlockPosition;

public class InteractListener implements Listener {

    private final BarrelManager barrelManager;

    public InteractListener(@NotNull BarrelManager barrelManager) {
        this.barrelManager = barrelManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final var interactedBlock = event.getClickedBlock();
        if (interactedBlock.getType() != Material.BARREL) {
            return;
        }
        final var player = event.getPlayer();
        if (player.isSneaking() && event.isBlockInHand()) {
            return;
        }
        final var position = BlockPosition.from(interactedBlock.getLocation());
        final var xpBarrel = barrelManager.getBarrelAt(interactedBlock.getWorld().getName(), position);
        if (xpBarrel == null) {
            return;
        }
        event.setCancelled(true);
    }

}
