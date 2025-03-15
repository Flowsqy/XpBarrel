package fr.flowsqy.xpbarrel.menu;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.abstractmenu.inventory.EventInventory.RegisterHandler;
import fr.flowsqy.abstractmenu.item.ItemBuilder;

public class MainMenuRegisterHandler implements RegisterHandler {

    private final ConfigurationSection itemSection;

    public MainMenuRegisterHandler(@NotNull ConfigurationSection inventorySection) {
        this.itemSection = inventorySection.getConfigurationSection("items");
    }

    @Override
    public void handle(EventInventory inventory, String key, ItemBuilder itemBuilder, List<Integer> slots) {
        if (itemSection == null) {
            inventory.register(itemBuilder, slots);
            return;
        }
        final var section = itemSection.getConfigurationSection(key);
        if (section == null) {
            inventory.register(itemBuilder, slots);
            return;
        }
    }

}
