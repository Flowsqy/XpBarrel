package fr.flowsqy.xpbarrel.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.barrel.BarrelManager;
import fr.flowsqy.xpbarrel.barrel.BlockPosition;
import fr.flowsqy.xpbarrel.config.MessageConfig;
import fr.flowsqy.xpbarrel.config.MessagesContainer;
import fr.flowsqy.xpbarrel.menu.MenuManager;
import net.md_5.bungee.api.chat.BaseComponent;

public class InteractListener implements Listener {

    private final XpBarrelPlugin plugin;
    private final BarrelManager barrelManager;
    private final MenuManager menuManager;
    private final InteractMessagesContainer messagesContainer;

    public InteractListener(@NotNull XpBarrelPlugin plugin, @NotNull BarrelManager barrelManager,
            @NotNull MenuManager menuManager, @NotNull InteractMessagesContainer messagesContainer) {
        this.plugin = plugin;
        this.barrelManager = barrelManager;
        this.menuManager = menuManager;
        this.messagesContainer = messagesContainer;
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
        // Avoid being called twice
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        final var playerId = player.getUniqueId();
        if (!xpBarrel.getOwner().equals(playerId) && !xpBarrel.getMembers().contains(playerId)
                && !player.hasPermission("xpbarrel.interact-other")) {
            if (messagesContainer.cantInteractOtherMessage != null) {
                player.spigot().sendMessage(messagesContainer.cantInteractOtherMessage);
            }
            return;
        }
        Bukkit.getScheduler().runTask(plugin, () -> {
            menuManager.addWatcher(player.getUniqueId(), xpBarrel);
            menuManager.openMainMenu(player, xpBarrel);
        });
    }

    public static class InteractMessagesContainer implements MessagesContainer {

        private BaseComponent cantInteractOtherMessage;

        @Override
        public void loadMessages(@NotNull MessageConfig messageConfig) {
            cantInteractOtherMessage = messageConfig.getComponentMessage("barrel.can-not-interact-other");
        }

    }

}
