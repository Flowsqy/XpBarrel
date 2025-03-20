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
import fr.flowsqy.xpbarrel.config.MessageConfig;
import fr.flowsqy.xpbarrel.config.MessagesContainer;
import net.md_5.bungee.api.chat.BaseComponent;

public class BreakListener implements Listener {

    private final BarrelManager barrelManager;
    private final ItemManager itemManager;
    private final BreakMessagesContainer messagesContainer;

    public BreakListener(@NotNull BarrelManager barrelManager, @NotNull ItemManager itemManager,
            @NotNull BreakMessagesContainer messagesContainer) {
        this.barrelManager = barrelManager;
        this.itemManager = itemManager;
        this.messagesContainer = messagesContainer;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
        if (!xpBarrel.getOwner().equals(breakerPlayer.getUniqueId())
                && !breakerPlayer.hasPermission("xpbarrel.break-other")) {
            event.setCancelled(true);
            if (messagesContainer.cantBreakMessage != null) {
                breakerPlayer.spigot().sendMessage(messagesContainer.cantBreakMessage);
            }
            return;
        }
        barrelManager.removeBarrelAt(worldName, position);
        event.setDropItems(false);
        final var dropItem = itemManager.generateItem(xpBarrel.getOwner(), xpBarrel.getExperience());
        if (dropItem != null) {
            world.dropItemNaturally(location.add(0.5, 0.5, 0.5), dropItem);
        }
    }

    public static class BreakMessagesContainer implements MessagesContainer {

        private BaseComponent cantBreakMessage;

        @Override
        public void loadMessages(@NotNull MessageConfig messageConfig) {
            cantBreakMessage = messageConfig.getComponentMessage("barrel.can-not-break-other");
        }

    }

}
