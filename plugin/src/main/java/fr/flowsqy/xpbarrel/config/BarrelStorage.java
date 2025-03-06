package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public class BarrelStorage {

    private YamlConfiguration oldConfiguration = null;
    private List<WorldConfiguration> configurations;

    private record WorldConfiguration(@NotNull String name, @NotNull YamlConfiguration configuration) {
    }

    public void load(@NotNull File dataFolder, @NotNull Logger logger) {
        final var oldStorage = new File(dataFolder, "storages.yml");
        if (oldStorage.exists() && oldStorage.isFile()) {
            oldConfiguration = YamlConfiguration.loadConfiguration(oldStorage);
            try {
                Files.move(oldStorage.toPath(), Path.of(dataFolder.getAbsolutePath(), "storages.yml.OLD"));
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Could not move the old configuration", e);
            }
        }
        final var storageDir = new File(dataFolder, "storage");
        if (storageDir.exists() && !storageDir.isDirectory()) {
            logger.log(Level.SEVERE, storageDir.getAbsolutePath() + " is not a directory");
            configurations = Collections.emptyList();
            return;
        }
        configurations = new LinkedList<>();
        for (var storageFile : storageDir.listFiles()) {
            final var fileName = storageFile.getName();
            if (!fileName.endsWith(".yml")) {
                continue;
            }
            final var yamlConfiguration = YamlConfiguration.loadConfiguration(storageFile);
            configurations.add(new WorldConfiguration(fileName.substring(0, fileName.length() - 4), yamlConfiguration));
        }
    }

}
