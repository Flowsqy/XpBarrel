package fr.flowsqy.xpbarrel.barrel;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.xpbarrel.XpBarrelPlugin;

public class ItemManager {

    private final NamespacedKey oldTypeKey;
    private final NamespacedKey oldOwnerKey;
    private final NamespacedKey oldExperienceKey;
    private final NamespacedKey barrelKey;
    private final NamespacedKey ownerKey;
    private final NamespacedKey experienceKey;

    public ItemManager(@NotNull XpBarrelPlugin plugin) {
        oldTypeKey = NamespacedKey.fromString("xpstorage:type");
        oldOwnerKey = NamespacedKey.fromString("xpstorage:owner");
        oldExperienceKey = NamespacedKey.fromString("xpstorage:storedexperience");
        barrelKey = new NamespacedKey(plugin, "barrel");
        ownerKey = new NamespacedKey(plugin, "owner");
        experienceKey = new NamespacedKey(plugin, "experience");
    }

    @NotNull
    public ItemStack generateItem(@Nullable UUID owner, int experience) {
        final var itemStack = new ItemStack(Material.BARREL);
        final var itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("XpBarrel");
        final var itemPdc = itemMeta.getPersistentDataContainer();
        final var barrelPdc = itemPdc.getAdapterContext().newPersistentDataContainer();
        if (owner != null) {
            barrelPdc.set(ownerKey, CustomPersistentDataType.UUID, owner);
        }
        barrelPdc.set(experienceKey, PersistentDataType.INTEGER, experience);
        itemPdc.set(barrelKey, PersistentDataType.TAG_CONTAINER, barrelPdc);
        return itemStack;
    }

    public record ExtractDataResult(@Nullable UUID owner, int experience) {
    }

    @Nullable
    public ExtractDataResult extractData(@NotNull ItemStack itemStack) {

        return null;
    }

}
