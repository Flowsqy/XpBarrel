package fr.flowsqy.xpbarrel.barrel;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.barrel.ExperienceCalculator.ExperienceData;

public class ItemManager {

    private final NamespacedKey oldTypeKey;
    private final NamespacedKey oldOwnerKey;
    private final NamespacedKey oldExperienceKey;
    private final NamespacedKey barrelKey;
    private final NamespacedKey ownerKey;
    private final NamespacedKey experienceKey;
    private String barrelName;
    private String[] barrelLore;

    public ItemManager(@NotNull XpBarrelPlugin plugin) {
        oldTypeKey = NamespacedKey.fromString("xpstorage:type");
        oldOwnerKey = NamespacedKey.fromString("xpstorage:owner");
        oldExperienceKey = NamespacedKey.fromString("xpstorage:storedexperience");
        barrelKey = new NamespacedKey(plugin, "barrel");
        ownerKey = new NamespacedKey(plugin, "owner");
        experienceKey = new NamespacedKey(plugin, "experience");
        barrelName = null;
        barrelLore = null;
    }

    public void load(@NotNull String barrelName, @NotNull String[] barrelLore) {
        this.barrelName = barrelName;
        this.barrelLore = barrelLore;
    }

    @NotNull
    public ItemStack generateItem(@Nullable UUID owner, int experience) {
        final var itemStack = new ItemStack(Material.BARREL);
        final var itemMeta = itemStack.getItemMeta();
        replaceNameAndLore(itemMeta, owner, experience);
        final var itemPdc = itemMeta.getPersistentDataContainer();
        final var barrelPdc = itemPdc.getAdapterContext().newPersistentDataContainer();
        if (owner != null) {
            barrelPdc.set(ownerKey, CustomPersistentDataType.UUID, owner);
        }
        barrelPdc.set(experienceKey, PersistentDataType.INTEGER, experience);
        itemPdc.set(barrelKey, PersistentDataType.TAG_CONTAINER, barrelPdc);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void replaceNameAndLore(@NotNull ItemMeta itemMeta, @Nullable UUID owner, int experience) {
        final var expCalculator = new ExperienceCalculator();
        final var experienceData = expCalculator.getTotalExperience(experience);
        final var name = replaceExperience(barrelName, experienceData, experience);
        final var lore = new String[barrelLore.length];
        for (int i = 0; i < barrelLore.length; i++) {
            lore[i] = replaceExperience(barrelLore[i], experienceData, experience);
        }
        final var ownerName = getName(owner);
        if (ownerName != null) {
            name.replace("%owner%", ownerName);
            for (int i = 0; i < barrelLore.length; i++) {
                lore[i] = lore[i].replace("%owner%", ownerName);
            }
        }
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));
    }

    private String getName(@Nullable UUID owner) {
        if (owner == null) {
            return null;
        }
        final var player = Bukkit.getOfflinePlayer(owner);
        return player.getName();
    }

    private String replaceExperience(@NotNull String original, @NotNull ExperienceData experienceData, int experience) {
        return original.replace("%level%", String.valueOf(experienceData.level()))
                .replace("%experience%", String.valueOf(experienceData.addedExperience()))
                .replace("%total-experience%", String.valueOf(experience));
    }

    public record ExtractDataResult(@Nullable UUID owner, int experience) {
    }

    @Nullable
    public ExtractDataResult extractData(@NotNull ItemStack itemStack) {
        final var itemMeta = itemStack.getItemMeta();
        final var itemPdc = itemMeta.getPersistentDataContainer();
        final var barrelPdc = itemPdc.get(barrelKey, PersistentDataType.TAG_CONTAINER);
        if (barrelPdc == null) {
            return extractOldData(itemStack, itemPdc);
        }
        final var owner = barrelPdc.get(ownerKey, CustomPersistentDataType.UUID);
        final int experience = barrelPdc.getOrDefault(experienceKey, PersistentDataType.INTEGER, 0);
        return new ExtractDataResult(owner, experience);
    }

    @Nullable
    public ExtractDataResult extractOldData(@NotNull ItemStack itemStack, @NotNull PersistentDataContainer itemPdc) {
        final var type = itemPdc.get(oldTypeKey, PersistentDataType.STRING);
        if (type == null || !type.equals("xp-barrel")) {
            return null;
        }
        final int experience = itemPdc.getOrDefault(oldExperienceKey, PersistentDataType.INTEGER, 0);
        final var rawOwner = itemPdc.get(oldOwnerKey, PersistentDataType.STRING);
        UUID owner = null;
        if (rawOwner != null) {
            try {
                owner = UUID.fromString(rawOwner);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return new ExtractDataResult(owner, experience);
    }

}
