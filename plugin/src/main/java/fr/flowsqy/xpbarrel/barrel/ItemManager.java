package fr.flowsqy.xpbarrel.barrel;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.xpbarrel.XpBarrelPlugin;

public class ItemManager {

    public ItemManager(@NotNull XpBarrelPlugin plugin) {
    }

    @NotNull
    public ItemStack generateItem(@Nullable UUID owner, int experience) {
        final var itemStack = new ItemStack(Material.BARREL);
        final var itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("XpBarrel");
        // TODO Set in pdc
        return itemStack;
    }

    public record ExtractDataResult(@Nullable UUID owner, int experience) {
    }

    @Nullable
    public ExtractDataResult extractData(@NotNull ItemStack itemStack) {
        // TODO Extract from pdc
        return null;
    }

}
