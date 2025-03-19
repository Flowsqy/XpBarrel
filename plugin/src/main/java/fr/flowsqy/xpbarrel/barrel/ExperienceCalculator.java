package fr.flowsqy.xpbarrel.barrel;

import org.jetbrains.annotations.NotNull;

public class ExperienceCalculator {

    public record ExperienceData(int level, int addedExperience) {
    }

    public int getTotalExpRequiredToLevel(int level) {
        if (level < 0) {
            return 0;
        }
        if (level < 17) {
            return level * (level + 6);
        }
        if (level < 32) {
            return ((level * (5 * level - 81)) >> 1) + 360;
        }
        return ((level * (9 * level - 325)) >> 1) + 2220;
    }

    public int getExpRequiredToLevelUp(int level) {
        if (level < 0) {
            return 0;
        }
        if (level < 16) {
            return 2 * level + 7;
        }
        if (level < 31) {
            return 5 * level - 38;
        }
        return 9 * level - 158;
    }

    @NotNull
    public ExperienceData getTotalExperience(int totalExperience) {
        if (totalExperience < 0) {
            return new ExperienceData(0, 0);
        }
        final double total = totalExperience;
        final double level;
        if (totalExperience < 353) {
            level = Math.sqrt(total + 9) - 3;
        } else if (totalExperience < 1508) {
            level = 8.1 + Math.sqrt(0.4 * (total - (7839 / 40)));
        } else {
            level = (325 / 18) + Math.sqrt((2 / 9) * (total - (54215 / 72)));
        }
        final int exactLevel = (int) level;
        final int totalExperienceOfLevel = getTotalExpRequiredToLevel(exactLevel);
        final int totalExperienceOfNextLevel = totalExperienceOfLevel + getExpRequiredToLevelUp(exactLevel);
        if (totalExperience < totalExperienceOfLevel) {
            final int totalExperienceOfPreviousLevel = getTotalExpRequiredToLevel(exactLevel - 1);
            if (totalExperience < totalExperienceOfPreviousLevel) {
                return new ExperienceData(0, totalExperience);
            }
            return new ExperienceData(exactLevel - 1, totalExperience - totalExperienceOfPreviousLevel);
        }
        if (totalExperience >= totalExperienceOfNextLevel) {
            final int totalExperienceOfNextNextLevel = totalExperienceOfNextLevel
                    + getExpRequiredToLevelUp(exactLevel + 1);
            if (totalExperience >= totalExperienceOfNextNextLevel) {
                return new ExperienceData(0, totalExperience);
            }
            return new ExperienceData(exactLevel + 1, totalExperience - totalExperienceOfNextLevel);
        }
        return new ExperienceData(exactLevel, totalExperienceOfLevel - totalExperience);
    }

    // Mimic mojang floor method to ensure correctness
    private int floor(float f) {
        final int i = (int) f;
        return f < (float) i ? i - 1 : i;
    }

    public int getExperienceFromProgression(int xpNeededForNextLevel, float progression) {
        return floor(progression * (float) xpNeededForNextLevel);
    }

}
