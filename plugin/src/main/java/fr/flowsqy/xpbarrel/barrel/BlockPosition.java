package fr.flowsqy.xpbarrel.barrel;

import org.bukkit.Location;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

public record BlockPosition(int x, int y, int z) {

    @NotNull
    public static BlockPosition from(@NotNull BlockVector blockVector) {
        return new BlockPosition(blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ());
    }

    @NotNull
    public static BlockPosition from(@NotNull Location location) {
        return new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

}
