package fr.flowsqy.xpbarrel.barrel;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemManager {

    @NotNull
    public ItemStack generateItem(@Nullable UUID owner, int experience) {
        return null;
    }

    public record ExtractDataResult(@Nullable UUID owner, int experience) {
    }

    @Nullable
    public ExtractDataResult extractData(@NotNull ItemStack itemStack) {
        return null;
    }

}
