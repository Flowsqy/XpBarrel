package fr.flowsqy.xpbarrel.barrel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public class XpBarrel {

    private final UUID owner;
    private int experience;
    private final Set<UUID> members;
    private final UUID watchingId;
    private final Set<UUID> watchers;

    public XpBarrel(@NotNull UUID owner, int experience, @NotNull Collection<UUID> members) {
        this.owner = owner;
        this.experience = experience;
        this.members = new HashSet<>(members);
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

    public int put(int amount, int maxExperience) {
        final int possibleNewExperience = experience + amount;
        experience = Math.min(possibleNewExperience, maxExperience);
        return possibleNewExperience - experience;
    }

    public boolean addMember(@NotNull UUID memberId) {
        return members.add(memberId);
    }

    public boolean removeMember(@NotNull UUID memberId) {
        return members.remove(memberId);
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
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
