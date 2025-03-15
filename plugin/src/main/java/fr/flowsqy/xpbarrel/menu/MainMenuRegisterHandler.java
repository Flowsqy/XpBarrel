package fr.flowsqy.xpbarrel.menu;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.abstractmenu.inventory.EventInventory.RegisterHandler;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.xpbarrel.XpBarrelPlugin;

public class MainMenuRegisterHandler implements RegisterHandler {

    private final XpBarrelPlugin plugin;
    private final ConfigurationSection itemSection;

    public MainMenuRegisterHandler(@NotNull XpBarrelPlugin plugin, @NotNull ConfigurationSection inventorySection) {
        this.plugin = plugin;
        this.itemSection = inventorySection.getConfigurationSection("items");
    }

    @Override
    public void handle(EventInventory inventory, String key, ItemBuilder itemBuilder, List<Integer> slots) {
        switch (key) {
            case "barrel":
                break;
        }
        if (itemSection == null) {
            inventory.register(itemBuilder, slots);
            return;
        }
        final var section = itemSection.getConfigurationSection(key);
        if (section == null) {
            inventory.register(itemBuilder, slots);
            return;
        }
        Consumer<InventoryClickEvent> eventHandler = null;
        if (section.getBoolean("close", false)) {
            eventHandler = tryChain(eventHandler, e -> e.getWhoClicked().closeInventory());
        }
        eventHandler = tryChain(eventHandler, getExperienceEvent(itemSection));
        if (eventHandler == null) {
            inventory.register(itemBuilder, slots);
            return;
        }
        final var finalEventHandler = eventHandler;
        inventory.register(itemBuilder, e -> Bukkit.getScheduler().runTask(plugin, () -> finalEventHandler.accept(e)),
                slots);
    }

    @Nullable
    private Consumer<InventoryClickEvent> tryChain(@Nullable Consumer<InventoryClickEvent> currentHandler,
            @Nullable Consumer<InventoryClickEvent> newHandler) {
        if (currentHandler == null) {
            return newHandler;
        }
        if (newHandler == null) {
            return currentHandler;
        }
        return currentHandler.andThen(newHandler);
    }

    @Nullable
    private Consumer<InventoryClickEvent> getExperienceEvent(@NotNull ConfigurationSection itemSection) {
        final var experienceSection = itemSection.getConfigurationSection("experience");
        if (experienceSection == null) {
            return null;
        }
        final var rawType = experienceSection.getString("type");
        final boolean put = rawType != null && rawType.equals("put");
        if (experienceSection.getBoolean("all", false)) {
            // Transfer all for specifiedAction
            return null;
        }
        final int value = experienceSection.getInt("value", -1);
        if (value < 1) {
            return null;
        }
        final boolean raw = experienceSection.getBoolean("raw", false);
        // Transfer value for specified action and type
        return null;
    }

}
