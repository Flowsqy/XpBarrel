package fr.flowsqy.xpbarrel.barrel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public class XpBarrel {

    private final UUID owner;
    private int experience;
    private final UUID watchingId;
    private final Set<UUID> watchers;

    public XpBarrel(@NotNull UUID owner, int experience) {
        this.owner = owner;
        this.experience = experience;
        watchingId = UUID.randomUUID();
        watchers = new HashSet<>();
    }

    @NotNull
    public UUID getOwner() {
        return owner;
    }

    public int getExperience() {
        return experience;
    }

    public int takeAll() {
        final int exp = experience;
        experience = 0;
        return exp;
    }

    public int take(int amount) {
        if (experience <= amount) {
            return takeAll();
        }
        experience -= amount;
        return amount;
    }

    public void put(int amount) {
        experience += amount;
    }

    @NotNull
    public UUID getWatchingId() {
        return watchingId;
    }

    public void addWatcher(@NotNull UUID playerId) {
        watchers.add(playerId);
    }

    public void removeWatcher(@NotNull UUID playerId) {
        watchers.remove(playerId);
    }

    public Set<UUID> getWatchers() {
        return Collections.unmodifiableSet(watchers);
    }

}
