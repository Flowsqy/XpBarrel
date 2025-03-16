package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.BarrelManager;

public class BarrelStorageSaver {

    private File storageDir;

    public void load(@NotNull File dataFolder, @NotNull Logger logger) {
        storageDir = new File(dataFolder, "storage");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
            return;
        }
        if (!storageDir.isDirectory()) {
            logger.log(Level.SEVERE, storageDir.getAbsolutePath() + " is not a directory");
            return;
        }
    }

    public void saveBarrels(@NotNull Logger logger, @NotNull File dataFolder,
            @NotNull BarrelManager.LoadedBarrelsSnapshot[] loadedBarrels) {
        for (var loadedBarrelsSnapshot : loadedBarrels) {
            final var configuration = new YamlConfiguration();
            int i = 0;
            for (var loadedBarrelSnapshot : loadedBarrelsSnapshot.loadedBarrelsInWorld()) {
                final var barrelSection = configuration.createSection("barrel-" + i++);
                final var xpBarrel = loadedBarrelSnapshot.xpBarrel();
                barrelSection.set("owner", xpBarrel.getOwner().toString());
                final var positionSection = barrelSection.createSection("position");
                final var position = loadedBarrelSnapshot.position();
                positionSection.set("x", position.x());
                positionSection.set("y", position.y());
                positionSection.set("z", position.z());
                barrelSection.set("experience", xpBarrel.getExperience());
            }
            final var configFile = new File(storageDir, loadedBarrelsSnapshot.world() + ".yml");
            try {
                configuration.save(configFile);
            } catch (IOException e) {
                logger.log(Level.WARNING,
                        "Couldn't save the barrels for the world '" + loadedBarrelsSnapshot.world() + "'", e);
                continue;
            }
        }
    }

}
