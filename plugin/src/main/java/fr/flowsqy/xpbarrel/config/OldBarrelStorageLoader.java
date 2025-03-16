package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import fr.flowsqy.xpbarrel.barrel.BlockPosition;
import fr.flowsqy.xpbarrel.barrel.XpBarrel;

public class OldBarrelStorageLoader {

    private Object yamlObject;

    public OldBarrelStorageLoader() {
        yamlObject = null;
    }

    public void load(@NotNull File dataFolder, @NotNull Logger logger) {
        final var oldStorage = new File(dataFolder, "storages.yml");
        if (!oldStorage.exists() || !oldStorage.isFile()) {
            return;
        }
        final var yaml = new Yaml();
        final InputStream inputStream;
        try {
            inputStream = new FileInputStream(oldStorage);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Couldn't find the old configuration", e);
            return;
        }
        yamlObject = yaml.load(inputStream);
        try {
            Files.move(oldStorage.toPath(), Path.of(dataFolder.getAbsolutePath(), "storages.yml.OLD"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not move the old configuration", e);
        }
    }

    @NotNull
    public void fillPreviousValues(@NotNull Map<String, Map<BlockPosition, XpBarrel>> loadedBarrels) {
        if (!(yamlObject instanceof Map rootMap)) {
            return;
        }
        final var rawInstances = rootMap.get("Instances");
        if (!(rawInstances instanceof List instancesList)) {
            return;
        }
        for (var rawEntry : instancesList) {
            if (!(rawEntry instanceof Map entryMap)) {
                continue;
            }
            final var rawType = entryMap.get("Type");
            if (!(rawType instanceof String type)) {
                continue;
            }
            if (!type.equals("XP_BARREL")) {
                continue;
            }
            final var rawOwner = entryMap.get("Owner");
            if (!(rawOwner instanceof String owner)) {
                continue;
            }
            final UUID ownerId;
            try {
                ownerId = UUID.fromString(owner);
            } catch (IllegalArgumentException e) {
                continue;
            }
            final var rawLocation = entryMap.get("Location");
            if (!(rawLocation instanceof Map locationMap)) {
                continue;
            }
            final var rawWorld = locationMap.get("world");
            if (!(rawWorld instanceof String world)) {
                continue;
            }
            final var rawX = locationMap.get("x");
            if (!(rawX instanceof Number x)) {
                continue;
            }
            final var rawY = locationMap.get("y");
            if (!(rawY instanceof Number y)) {
                continue;
            }
            final var rawZ = locationMap.get("z");
            if (!(rawZ instanceof Number z)) {
                continue;
            }
            int experience = 0;
            if (entryMap.get("StoredExperience") instanceof Number storedExperience) {
                experience = storedExperience.intValue();
            }
            final var blockPosition = new BlockPosition(x.intValue(), y.intValue(), z.intValue());
            final var loadedBarrelsInWorld = loadedBarrels.computeIfAbsent(world, k -> new HashMap<>());
            loadedBarrelsInWorld.put(blockPosition, new XpBarrel(ownerId, experience));
        }
    }

}
