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

    private int getRawLevelFromTotalExperience(int totalExperience) {
        final double total = totalExperience;
        final double level;
        if (totalExperience < 353) {
            level = Math.sqrt(total + 9d) - 3d;
        } else if (totalExperience < 1508) {
            level = 8.1d + Math.sqrt(0.4d * (total - (7839d / 40d)));
        } else {
            level = (325d / 18d) + Math.sqrt((2d / 9d) * (total - (54215d / 72d)));
        }
        return (int) level;
    }

    @NotNull
    public ExperienceData getTotalExperience(int totalExperience) {
        if (totalExperience < 0) {
            return new ExperienceData(0, 0);
        }
        int level = getRawLevelFromTotalExperience(totalExperience);
        int totalExperienceOfLevel = getTotalExpRequiredToLevel(level);
        final int totalExperienceOfNextLevel = totalExperienceOfLevel + getExpRequiredToLevelUp(level);

        if (totalExperience < totalExperienceOfLevel) {
            final int totalExperienceOfPreviousLevel = totalExperienceOfLevel - getExpRequiredToLevelUp(level - 1);
            if (totalExperience < totalExperienceOfPreviousLevel) {
                return new ExperienceData(0, totalExperience);
            }
            level--;
            totalExperienceOfLevel = totalExperienceOfPreviousLevel;
        } else if (totalExperience >= totalExperienceOfNextLevel) {
            final int totalExperienceOfNextNextLevel = totalExperienceOfNextLevel
                    + getExpRequiredToLevelUp(level + 1);
            if (totalExperience >= totalExperienceOfNextNextLevel) {
                return new ExperienceData(0, totalExperience);
            }
            level++;
            totalExperienceOfLevel = totalExperienceOfNextLevel;
        }
        return new ExperienceData(level, totalExperience - totalExperienceOfLevel);
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
