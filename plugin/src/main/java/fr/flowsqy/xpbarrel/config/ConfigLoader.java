package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.jetbrains.annotations.NotNull;

public class ConfigLoader {

    public boolean checkDataFolder(@NotNull File dataFolder) {
        if (dataFolder.exists())
            return dataFolder.canWrite();
        return dataFolder.mkdirs();
    }

    @NotNull
    public File initFile(@NotNull File dataFolder, @NotNull InputStream resource, @NotNull String fileName) {
        final File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try {
                Files.copy(resource, file.toPath());
            } catch (IOException ignored) {
            }
        }
        return file;
    }

    @NotNull
    public File initFile(@NotNull File dataFolder, @NotNull String fileName) {
        final File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }
        return file;
    }

}
