package fr.flowsqy.xpbarrel.barrel;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarrelManager {

    private final Map<String, Map<BlockPosition, XpBarrel>> loadedBarrels;

    public BarrelManager() {
        loadedBarrels = new HashMap<>();
    }

    public void load(@NotNull Map<String, Map<BlockPosition, XpBarrel>> loadedBarrels) {
        this.loadedBarrels.clear();
        this.loadedBarrels.putAll(loadedBarrels);
    }

    @Nullable
    public XpBarrel addBarrelAt(@NotNull String world, @NotNull BlockPosition barrelPosition,
            @NotNull XpBarrel xpBarrel) {
        final var loadedBarrelsInWorld = loadedBarrels.computeIfAbsent(world, k -> new HashMap<>());
        return loadedBarrelsInWorld.put(barrelPosition, xpBarrel);
    }

    @Nullable
    public XpBarrel getBarrelAt(@NotNull String world, @NotNull BlockPosition barrelPosition) {
        final var loadedBarrelsInWorld = loadedBarrels.get(world);
        if (loadedBarrelsInWorld == null) {
            return null;
        }
        return loadedBarrelsInWorld.get(barrelPosition);
    }

    @Nullable
    public XpBarrel removeBarrelAt(@NotNull String world, @NotNull BlockPosition barrelPosition) {
        final var loadedBarrelsInWorld = loadedBarrels.get(world);
        if (loadedBarrelsInWorld == null) {
            return null;
        }
        return loadedBarrelsInWorld.remove(barrelPosition);
    }

    @NotNull
    public LoadedBarrelsSnapshot[] getSnapshot() {
        final int worldCount = loadedBarrels.size();
        final var loadedBarrelsSnapshot = new LoadedBarrelsSnapshot[worldCount];
        int worldNumber = 0;
        for (var worldEntry : loadedBarrels.entrySet()) {
            final var loadedBarrelsInWorld = worldEntry.getValue();
            final int loadedBarrelsInWorldCount = loadedBarrelsInWorld.size();
            final var loadedBarrelsInWorldSnapshot = new LoadedBarrelSnapshot[loadedBarrelsInWorldCount];
            int barrelNumber = 0;
            for (var barrelEntry : loadedBarrelsInWorld.entrySet()) {
                loadedBarrelsInWorldSnapshot[barrelNumber++] = new LoadedBarrelSnapshot(barrelEntry.getKey(),
                        barrelEntry.getValue());
            }
            loadedBarrelsSnapshot[worldNumber++] = new LoadedBarrelsSnapshot(worldEntry.getKey(),
                    loadedBarrelsInWorldSnapshot);
        }
        return loadedBarrelsSnapshot;
    }

    public record LoadedBarrelSnapshot(@NotNull BlockPosition position, @NotNull XpBarrel xpBarrel) {
    }

    public record LoadedBarrelsSnapshot(@NotNull String world, @NotNull LoadedBarrelSnapshot[] loadedBarrelsInWorld) {
    }

}
