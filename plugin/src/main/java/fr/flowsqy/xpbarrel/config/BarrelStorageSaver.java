package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.BlockPosition;
import fr.flowsqy.xpbarrel.barrel.XpBarrelSnapshot;

public class BarrelStorageSaver {

    public void load(@NotNull File dataFolder, @NotNull Logger logger) {
        final var storageDir = new File(dataFolder, "storage");
        if (!storageDir.exists()) {
            return;
        }
        if (!storageDir.isDirectory()) {
            logger.log(Level.SEVERE, storageDir.getAbsolutePath() + " is not a directory");
            return;
        }
    }

    public void saveBarrels(@NotNull Logger logger, @NotNull File dataFolder,
            @NotNull Map<String, Map<BlockPosition, XpBarrelSnapshot>> loadedBarrels) {
        for (var worldEntry : loadedBarrels.entrySet()) {
            final var configuration = new YamlConfiguration();
            int i = 0;
            for (var barrelEntry : worldEntry.getValue().entrySet()) {
                final var barrelSection = configuration.createSection("barrel-" + i++);
                final var xpBarrel = barrelEntry.getValue();
                barrelSection.set("owner", xpBarrel.owner());
                final var positionSection = configuration.createSection("position");
                final var position = barrelEntry.getKey();
                positionSection.set("x", position.x());
                positionSection.set("y", position.y());
                positionSection.set("z", position.z());
                barrelSection.set("experience", xpBarrel.experience());
            }
            final var configFile = new File(dataFolder, worldEntry.getKey() + ".yml");
            try {
                configuration.save(configFile);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Couldn't save the barrels for the world '" + worldEntry.getKey() + "'", e);
                continue;
            }
        }
    }

}
