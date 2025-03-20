package fr.flowsqy.xpbarrel.menu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
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
import fr.flowsqy.xpbarrel.config.MessageConfig;
import fr.flowsqy.xpbarrel.conversation.ConversationBuilder;

public class MainMenuRegisterHandler implements RegisterHandler {

    private final XpBarrelPlugin plugin;
    private final MenuManager menuManager;
    private final ConfigurationSection itemSection;
    private final int maxExperience;
    private final ConversationBuilder conversationBuilder;
    private final String showMembersMessage, noMembersMessage, notMemberMessage, memberRemovedMessage, askMemberRemoveMessage,
            notOnlinePlayerMessage, alreadyMemberMessage, memberAddedMessage, askMemberAddMessage;

    public MainMenuRegisterHandler(@NotNull XpBarrelPlugin plugin, @NotNull MenuManager menuManager,
            @NotNull ConfigurationSection inventorySection, int maxExperience, @NotNull MessageConfig messageConfig,
            @NotNull ConversationBuilder conversationBuilder) {
        this.plugin = plugin;
        this.menuManager = menuManager;
        this.itemSection = inventorySection.getConfigurationSection("items");
        this.maxExperience = maxExperience;
        this.conversationBuilder = conversationBuilder;
        showMembersMessage = messageConfig.getMessage("barrel.members.show");
        noMembersMessage = messageConfig.getMessage("barrel.members.no-members");
        notMemberMessage = messageConfig.getMessage("barrel.members.not-member");
        memberRemovedMessage = messageConfig.getMessage("barrel.members.removed");
        final var askMemberRemoveMessage = messageConfig.getMessage("barrel.members.ask-remove");
        this.askMemberRemoveMessage = askMemberRemoveMessage == null ? "Which member do you want to remove ?"
                : askMemberRemoveMessage;
        notOnlinePlayerMessage = messageConfig.getMessage("barrel.members.not-online");
        alreadyMemberMessage = messageConfig.getMessage("barrel.members.already-member");
        memberAddedMessage = messageConfig.getMessage("barrel.members.added");
        final var askMemberAddMessage = messageConfig.getMessage("barrel.members.ask-add");
        this.askMemberAddMessage = askMemberAddMessage == null ? "Which player do you want to add ?"
                : askMemberAddMessage;
    }

    @Override
    public void handle(EventInventory inventory, String key, ItemBuilder itemBuilder, List<Integer> slots) {
        switch (key) {
            case "barrel":
                itemBuilder.creatorListener(new CreatorAdaptor() {

                    private String ownerName;
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
                        final var ownerPlayer = Bukkit.getOfflinePlayer(xpBarrel.getOwner());
                        ownerName = ownerPlayer.getName() == null ? ownerPlayer.getUniqueId().toString()
                                : ownerPlayer.getName();
                    }

                    @Override
                    public void close(Player player) {
                        experienceData = null;
                        experience = 0;
                        ownerName = null;
                    }

                    @Override
                    public List<String> handleLore(Player player, List<String> lore) {
                        if (lore == null) {
                            return null;
                        }
                        final var newLore = new LinkedList<String>();
                        for (var line : lore) {
                            newLore.add(
                                    replaceExperience(line, experienceData, experience).replace("%owner%", ownerName));
                        }
                        return newLore;
                    }

                    @Override
                    public String handleName(Player player, String name) {
                        return name == null ? null
                                : replaceExperience(name, experienceData, experience).replace("%owner%", ownerName);
                    }

                    private String replaceExperience(@NotNull String original, @NotNull ExperienceData experienceData,
                            int experience) {
                        return original.replace("%level%", String.valueOf(experienceData.level()))
                                .replace("%experience%", String.valueOf(experienceData.addedExperience()))
                                .replace("%total-experience%", String.valueOf(experience));
                    }

                });
                break;
            case "members-show":
                final Consumer<InventoryClickEvent> showMembersEventHandler = e -> {
                    final var humanEntity = e.getWhoClicked();
                    if (!(humanEntity instanceof Player player)) {
                        return;
                    }
                    final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                    if (xpBarrel == null) {
                        return;
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.closeInventory();
                        if (showMembersMessage == null) {
                            return;
                        }
                        final var members = xpBarrel.getMembers();
                        if (members.isEmpty()) {
                            if (noMembersMessage != null) {
                                player.sendMessage(noMembersMessage);
                            }
                            return;
                        }
                        boolean first = true;
                        final var sb = new StringBuilder();
                        for (var member : members) {
                            if (first) {
                                first = false;
                            } else {
                                sb.append(", ");
                            }
                            final var memberPlayer = Bukkit.getOfflinePlayer(member);
                            sb.append(memberPlayer.getName() == null ? member.toString() : memberPlayer.getName());
                        }
                        player.sendMessage(showMembersMessage.replace("%members%", sb.toString()));
                    });
                };
                inventory.register(itemBuilder, showMembersEventHandler, slots);
                return;
            case "members-remove":
                itemBuilder.creatorListener(new CreatorAdaptor() {

                    private boolean display;

                    @Override
                    public void open(Player player) {
                        final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                        if (xpBarrel == null) {
                            return;
                        }
                        display = xpBarrel.getOwner().equals(player.getUniqueId())
                                || player.hasPermission("xpbarrel.modify-members-other");
                    }

                    @Override
                    public void close(Player player) {
                        display = false;
                    }

                    @Override
                    public Material handleMaterial(Player player, Material material) {
                        return display ? material : null;
                    }

                });

                final Consumer<InventoryClickEvent> removeMemberEventHandler = e -> {
                    final var humanEntity = e.getWhoClicked();
                    if (!(humanEntity instanceof Player player)) {
                        return;
                    }
                    final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                    if (xpBarrel == null) {
                        return;
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.closeInventory();
                        final var inputMap = new HashMap<String, UUID>();
                        for (var memberId : xpBarrel.getMembers()) {
                            final var member = Bukkit.getOfflinePlayer(memberId).getName();
                            if (member == null) {
                                continue;
                            }
                            inputMap.put(member, memberId);
                        }
                        final var conversation = conversationBuilder.buildConversation(player, new StringPrompt() {

                            @Override
                            @Nullable
                            public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
                                final var matchingId = inputMap.get(input);
                                if (matchingId == null || !xpBarrel.removeMember(matchingId)) {
                                    if (notMemberMessage != null) {
                                        context.getForWhom().sendRawMessage(notMemberMessage.replace("%input%", input));
                                    }
                                    return Prompt.END_OF_CONVERSATION;
                                }
                                if (memberRemovedMessage != null) {
                                    context.getForWhom()
                                            .sendRawMessage(memberRemovedMessage.replace("%member%", input));
                                }
                                return Prompt.END_OF_CONVERSATION;
                            }

                            @Override
                            @NotNull
                            public String getPromptText(@NotNull ConversationContext context) {
                                return askMemberRemoveMessage;
                            }
                        });
                        conversation.begin();
                    });
                };
                inventory.register(itemBuilder, removeMemberEventHandler, slots);
                return;
            case "members-add":
                itemBuilder.creatorListener(new CreatorAdaptor() {

                    private boolean display;

                    @Override
                    public void open(Player player) {
                        final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                        if (xpBarrel == null) {
                            return;
                        }
                        display = xpBarrel.getOwner().equals(player.getUniqueId())
                                || player.hasPermission("xpbarrel.modify-members-other");
                    }

                    @Override
                    public void close(Player player) {
                        display = false;
                    }

                    @Override
                    public Material handleMaterial(Player player, Material material) {
                        return display ? material : null;
                    }

                });

                final Consumer<InventoryClickEvent> addMemberEventHandler = e -> {
                    final var humanEntity = e.getWhoClicked();
                    if (!(humanEntity instanceof Player player)) {
                        return;
                    }
                    final var xpBarrel = menuManager.getWatchedBarrel(player.getUniqueId());
                    if (xpBarrel == null) {
                        return;
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.closeInventory();
                        final var inputMap = new HashMap<String, UUID>();
                        for (var connectedPlayer : Bukkit.getOnlinePlayers()) {
                            if (connectedPlayer == null) {
                                continue;
                            }
                            inputMap.put(connectedPlayer.getName(), connectedPlayer.getUniqueId());
                        }
                        final var conversation = conversationBuilder.buildConversation(player, new StringPrompt() {

                            @Override
                            @Nullable
                            public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
                                final var matchingId = inputMap.get(input);
                                if (matchingId == null) {
                                    if (notOnlinePlayerMessage != null) {
                                        context.getForWhom()
                                                .sendRawMessage(notOnlinePlayerMessage.replace("%input%", input));
                                    }
                                    return Prompt.END_OF_CONVERSATION;
                                }
                                if (!xpBarrel.addMember(matchingId)) {
                                    if (alreadyMemberMessage != null) {
                                        context.getForWhom()
                                                .sendRawMessage(alreadyMemberMessage.replace("%member%", input));
                                    }
                                    return Prompt.END_OF_CONVERSATION;
                                }
                                if (memberAddedMessage != null) {
                                    context.getForWhom()
                                            .sendRawMessage(memberAddedMessage.replace("%member%", input));
                                }
                                return Prompt.END_OF_CONVERSATION;
                            }

                            @Override
                            @NotNull
                            public String getPromptText(@NotNull ConversationContext context) {
                                return askMemberAddMessage;
                            }
                        });
                        conversation.begin();
                    });
                };
                return;
        }
        if (itemSection == null)

        {
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
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 1.0f);
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
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 1.0f);
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
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 1.0f);
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
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 1.0f);
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
