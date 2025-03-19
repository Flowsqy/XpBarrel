package fr.flowsqy.xpbarrel.menu;

import java.util.LinkedList;
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
import fr.flowsqy.abstractmenu.item.CreatorAdaptor;
import fr.flowsqy.abstractmenu.item.ItemBuilder;
import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.barrel.ExperienceCalculator;
import fr.flowsqy.xpbarrel.barrel.ExperienceCalculator.ExperienceData;

public class MainMenuRegisterHandler implements RegisterHandler {

    private final XpBarrelPlugin plugin;
    private final MenuManager menuManager;
    private final ConfigurationSection itemSection;
    private final int maxExperience;

    public MainMenuRegisterHandler(@NotNull XpBarrelPlugin plugin, @NotNull MenuManager menuManager,
            @NotNull ConfigurationSection inventorySection, int maxExperience) {
        this.plugin = plugin;
        this.menuManager = menuManager;
        this.itemSection = inventorySection.getConfigurationSection("items");
        this.maxExperience = maxExperience;
    }

    @Override
    public void handle(EventInventory inventory, String key, ItemBuilder itemBuilder, List<Integer> slots) {
        switch (key) {
            case "barrel":
                itemBuilder.creatorListener(new CreatorAdaptor() {

                    private ExperienceData experienceData;
                    private int experience;

                    @Override
                    public void open(Player player) {
                        final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                        if (xpBarrel == null) {
                            return;
                        }
                        experience = xpBarrel.getExperience();
                        final var expCalculator = new ExperienceCalculator();
                        experienceData = expCalculator.getTotalExperience(experience);
                    }

                    @Override
                    public void close(Player player) {
                        experienceData = null;
                    }

                    @Override
                    public List<String> handleLore(Player player, List<String> lore) {
                        if (lore == null) {
                            return null;
                        }
                        final var newLore = new LinkedList<String>();
                        for (var line : lore) {
                            newLore.add(replaceExperience(line, experienceData, experience));
                        }
                        return newLore;
                    }

                    @Override
                    public String handleName(Player player, String name) {
                        return name == null ? null : replaceExperience(name, experienceData, experience);
                    }

                    private String replaceExperience(@NotNull String original, @NotNull ExperienceData experienceData,
                            int experience) {
                        return original.replace("%level%", String.valueOf(experienceData.level()))
                                .replace("%experience%", String.valueOf(experienceData.addedExperience()))
                                .replace("%total-experience%", String.valueOf(experience));
                    }

                });
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
                        final int remainingXp = xpBarrel.put(playerXpPoints, maxExperience);
                        if (remainingXp != playerXpPoints) {

                            player.setExp(0f);
                            player.setLevel(0);
                            player.giveExp(remainingXp);
                        }
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
                            final int remainingExperience = xpBarrel.put(playerXpPoints, maxExperience);
                            if (remainingExperience == playerXpPoints) {
                                return;
                            }
                            player.setExp(0f);
                            player.setLevel(0);
                            player.giveExp(remainingExperience);
                        } else {
                            final int remainingExperience = xpBarrel.put(value, maxExperience);
                            if (remainingExperience == value) {
                                return;
                            }
                            player.giveExp(remainingExperience - value);
                        }
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
                        final int totaPlayerXp = expCalculator.getTotalExpRequiredToLevel(playerLevel) + progressionXp;
                        final int remainingExperience = xpBarrel.put(totaPlayerXp, maxExperience);
                        if (totaPlayerXp == remainingExperience) {
                            return;
                        }
                        player.setExp(0f);
                        player.setLevel(0);
                        player.giveExp(remainingExperience);
                    } else {
                        final int newLevel = playerLevel - value;
                        final int experiencePut = progressionXp + expCalculator.getTotalExpRequiredToLevel(playerLevel)
                                - expCalculator.getTotalExpRequiredToLevel(newLevel);
                        final int remainingExperience = xpBarrel.put(experiencePut, maxExperience);
                        if (experiencePut == remainingExperience) {
                            return;
                        }
                        player.setExp(0f);
                        player.setLevel(newLevel);
                        player.giveExp(remainingExperience);
                    }
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
