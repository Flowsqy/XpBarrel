package fr.flowsqy.xpbarrel.menu;

import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.abstractmenu.inventory.EventInventory;
import fr.flowsqy.abstractmenu.inventory.EventInventory.RegisterHandler;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.barrel.ExperienceCalculator;

public class MainMenuRegisterHandler implements RegisterHandler {

    private final XpBarrelPlugin plugin;
    private final MenuManager menuManager;
    private final ConfigurationSection itemSection;

    public MainMenuRegisterHandler(@NotNull XpBarrelPlugin plugin, @NotNull MenuManager menuManager,
            @NotNull ConfigurationSection inventorySection) {
        this.plugin = plugin;
        this.menuManager = menuManager;
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
        eventHandler = tryChain(eventHandler, getExperienceEvent(inventory, section));
        if (section.getBoolean("close", false)) {
            eventHandler = tryChain(eventHandler,
                    e -> Bukkit.getScheduler().runTask(plugin, () -> e.getWhoClicked().closeInventory()));
        }
        if (eventHandler == null) {
            inventory.register(itemBuilder, slots);
            return;
        }
        inventory.register(itemBuilder, eventHandler, slots);
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
    private Consumer<InventoryClickEvent> getExperienceEvent(@NotNull final EventInventory eventInventory,
            @NotNull ConfigurationSection itemSection) {
        final var experienceSection = itemSection.getConfigurationSection("experience");
        if (experienceSection == null) {
            return null;
        }
        final var rawType = experienceSection.getString("type");
        final boolean put = rawType != null && rawType.equals("put");
        if (experienceSection.getBoolean("all", false)) {
            if (put) {
                return e -> {
                    final var humanEntity = e.getWhoClicked();
                    if (!(humanEntity instanceof Player player)) {
                        return;
                    }
                    final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                    if (xpBarrel == null) {
                        return;
                    }
                    final var expCalculator = new ExperienceCalculator();
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        final int playerLevel = player.getLevel();
                        final int xpNeededForNextLevel = expCalculator.getExpRequiredToLevelUp(playerLevel);
                        final int playerXpPoints = expCalculator.getTotalExpRequiredToLevel(playerLevel)
                                + expCalculator.getExperienceFromProgression(xpNeededForNextLevel, player.getExp());
                        xpBarrel.put(playerXpPoints);
                        player.setExp(0f);
                        player.setLevel(0);
                        for (var watcherId : xpBarrel.getWatchers()) {
                            final var watcher = Bukkit.getPlayer(watcherId);
                            if (watcher == null) {
                                continue;
                            }
                            eventInventory.refresh(xpBarrel.getWatchingId(), watcher);
                        }
                    });
                };
            }
            return e -> {
                final var humanEntity = e.getWhoClicked();
                if (!(humanEntity instanceof Player player)) {
                    return;
                }
                final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                if (xpBarrel == null) {
                    return;
                }
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.giveExp(xpBarrel.takeAll());
                    for (var watcherId : xpBarrel.getWatchers()) {
                        final var watcher = Bukkit.getPlayer(watcherId);
                        if (watcher == null) {
                            continue;
                        }
                        eventInventory.refresh(xpBarrel.getWatchingId(), watcher);
                    }
                });
            };
        }
        final int value = experienceSection.getInt("value", -1);
        if (value < 1) {
            return null;
        }
        final boolean raw = experienceSection.getBoolean("raw", false);
        if (raw) {
            if (put) {
                return e -> {
                    final var humanEntity = e.getWhoClicked();
                    if (!(humanEntity instanceof Player player)) {
                        return;
                    }
                    final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                    if (xpBarrel == null) {
                        return;
                    }
                    final var expCalculator = new ExperienceCalculator();
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        final int playerLevel = player.getLevel();
                        final int xpNeededToLevelUp = expCalculator.getExpRequiredToLevelUp(playerLevel);
                        final int playerXpPoints = expCalculator.getTotalExpRequiredToLevel(playerLevel)
                                + expCalculator.getExperienceFromProgression(xpNeededToLevelUp, player.getExp());
                        if (playerXpPoints <= value) {
                            xpBarrel.put(playerXpPoints);
                            player.setExp(0f);
                            player.setLevel(0);
                            return;
                        }
                        player.giveExp(-value);
                        xpBarrel.put(value);
                        for (var watcherId : xpBarrel.getWatchers()) {
                            final var watcher = Bukkit.getPlayer(watcherId);
                            if (watcher == null) {
                                continue;
                            }
                            eventInventory.refresh(xpBarrel.getWatchingId(), watcher);
                        }
                    });
                };
            }

            return e -> {
                final var humanEntity = e.getWhoClicked();
                if (!(humanEntity instanceof Player player)) {
                    return;
                }
                final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                if (xpBarrel == null) {
                    return;
                }
                Bukkit.getScheduler().runTask(plugin, () -> {
                    player.giveExp(xpBarrel.take(value));
                    for (var watcherId : xpBarrel.getWatchers()) {
                        final var watcher = Bukkit.getPlayer(watcherId);
                        if (watcher == null) {
                            continue;
                        }
                        eventInventory.refresh(xpBarrel.getWatchingId(), watcher);
                    }
                });
            };
        }
        if (put) {
            return e -> {
                final var humanEntity = e.getWhoClicked();
                if (!(humanEntity instanceof Player player)) {
                    return;
                }
                final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                if (xpBarrel == null) {
                    return;
                }
                final var expCalculator = new ExperienceCalculator();
                Bukkit.getScheduler().runTask(plugin, () -> {
                    final int playerLevel = player.getLevel();
                    final int xpNeededToLevelUp = expCalculator.getExpRequiredToLevelUp(playerLevel);
                    final int progressionXp = expCalculator.getExperienceFromProgression(xpNeededToLevelUp,
                            player.getExp());
                    if (value >= playerLevel) {
                        xpBarrel.put(expCalculator.getTotalExpRequiredToLevel(playerLevel) + progressionXp);
                        player.setExp(0f);
                        player.setLevel(0);
                        return;
                    }
                    final int newLevel = playerLevel - value;
                    player.setExp(0f);
                    player.setLevel(newLevel);
                    xpBarrel.put(progressionXp + expCalculator.getTotalExpRequiredToLevel(playerLevel)
                            - expCalculator.getTotalExpRequiredToLevel(newLevel));
                    for (var watcherId : xpBarrel.getWatchers()) {
                        final var watcher = Bukkit.getPlayer(watcherId);
                        if (watcher == null) {
                            continue;
                        }
                        eventInventory.refresh(xpBarrel.getWatchingId(), watcher);
                    }
                });
            };
        }
        return e -> {
            final var humanEntity = e.getWhoClicked();
            if (!(humanEntity instanceof Player player)) {
                return;
            }
            final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
            if (xpBarrel == null) {
                return;
            }
            final var expCalculator = new ExperienceCalculator();
            Bukkit.getScheduler().runTask(plugin, () -> {
                final int playerLevel = player.getLevel();
                final int xpNeededToLevelUp = expCalculator.getExpRequiredToLevelUp(playerLevel);
                final int progressionXp = expCalculator.getExperienceFromProgression(xpNeededToLevelUp,
                        player.getExp());
                int experienceToTake = xpNeededToLevelUp - progressionXp;
                if (value > 1) {
                    experienceToTake += expCalculator.getTotalExpRequiredToLevel(playerLevel + value)
                            - expCalculator.getTotalExpRequiredToLevel(playerLevel + 1);
                }
                player.giveExp(xpBarrel.take(experienceToTake));
                for (var watcherId : xpBarrel.getWatchers()) {
                    final var watcher = Bukkit.getPlayer(watcherId);
                    if (watcher == null) {
                        continue;
                    }
                    eventInventory.refresh(xpBarrel.getWatchingId(), watcher);
                }
            });
        };
    }

}
