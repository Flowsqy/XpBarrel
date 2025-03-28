package fr.flowsqy.xpbarrel.config;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.ExperienceCalculator;

public class Config {

    private YamlConfiguration configuration;

    public void load(@NotNull ConfigLoader configLoader, @NotNull JavaPlugin plugin, @NotNull String fileName) {
        final File configFile = configLoader.initFile(plugin.getDataFolder(),
                Objects.requireNonNull(plugin.getResource(fileName)), fileName);
        try {
            configuration = YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public String getBarrelName() {
        final var rawName = configuration.getString("item.name");
        if (rawName == null) {
            return ChatColor.RESET + "XpBarrel";
        }
        return ChatColor.translateAlternateColorCodes('&', rawName);
    }

    @NotNull
    public String[] getBarrelLore() {
        final var rawLore = configuration.getStringList("item.lore");
        final var lore = new String[rawLore.size()];
        final var iterator = rawLore.iterator();
        for (int i = 0; i < lore.length; i++) {
            lore[i] = ChatColor.translateAlternateColorCodes('&', iterator.next());
        }
        return lore;
    }

    public int getMaxExperience() {
        int rawMax = configuration.getInt("max-experience");
        if (rawMax < 1 || rawMax > 15460) {
            rawMax = 15460;
        }
        return new ExperienceCalculator().getTotalExpRequiredToLevel(rawMax);
    }

    @NotNull
    public Set<String> getConversationCancelWords() {
        return new HashSet<>(configuration.getStringList("conversation.cancel-words"));
    }

    public int getConversationTimeout() {
        return configuration.getInt("conversation.timeout", 60);
    }

}
