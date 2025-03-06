package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.BlockPosition;
import fr.flowsqy.xpbarrel.barrel.XpBarrelSnapshot;

public class BarrelStorageLoader {

    private final OldBarrelStorageLoader oldBarrelStorageLoader;
    private final List<WorldConfiguration> configurations;

    public BarrelStorageLoader() {
        oldBarrelStorageLoader = new OldBarrelStorageLoader();
        configurations = new LinkedList<>();
    }

    private record WorldConfiguration(@NotNull String name, @NotNull YamlConfiguration configuration) {
    }

    public void load(@NotNull File dataFolder, @NotNull Logger logger) {
        oldBarrelStorageLoader.load(dataFolder, logger);
        final var storageDir = new File(dataFolder, "storage");
        if (!storageDir.exists()) {
            return;
        }
        if (!storageDir.isDirectory()) {
            logger.log(Level.SEVERE, storageDir.getAbsolutePath() + " is not a directory");
            return;
        }
        for (var storageFile : storageDir.listFiles()) {
            final var fileName = storageFile.getName();
            if (!fileName.endsWith(".yml")) {
                continue;
            }
            final var yamlConfiguration = YamlConfiguration.loadConfiguration(storageFile);
            configurations.add(new WorldConfiguration(fileName.substring(0, fileName.length() - 4), yamlConfiguration));
        }
    }

    @NotNull
    public Map<String, Map<BlockPosition, XpBarrelSnapshot>> loadBarrels(@NotNull Logger logger) {
        final Map<String, Map<BlockPosition, XpBarrelSnapshot>> loadedBarrels = new HashMap<>();
        oldBarrelStorageLoader.fillPreviousValues(loadedBarrels);
        for (var worldConfig : configurations) {
            final var loadedBarrelsInWorld = loadedBarrels.computeIfAbsent(worldConfig.name(), k -> new HashMap<>());
            for (var key : worldConfig.configuration().getKeys(false)) {
                final var barrelSection = worldConfig.configuration().getConfigurationSection(key);
                if (barrelSection == null) {
                    continue;
                }
                final var rawOwner = barrelSection.getString("owner");
                final UUID owner;
                try {
                    owner = UUID.fromString(rawOwner);
                } catch (IllegalArgumentException e) {
                    logger.log(Level.WARNING, "Couldn't retrieve owner id", e);
                    continue;
                }
                final var positionSection = barrelSection.getConfigurationSection("position");
                if (positionSection == null) {
                    logger.warning("Couldn't retrieve the position in " + barrelSection.getCurrentPath() + " ("
                            + worldConfig.name() + ")");
                    continue;
                }
                final int x = positionSection.getInt("x", 0);
                final int y = positionSection.getInt("y", 0);
                final int z = positionSection.getInt("z", 0);
                final var position = new BlockPosition(x, y, z);
                final int experience = barrelSection.getInt("experience", 0);
                loadedBarrelsInWorld.put(position, new XpBarrelSnapshot(experience, owner));
            }
        }
        return loadedBarrels;
    }

}
