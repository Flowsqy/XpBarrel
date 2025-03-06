package fr.flowsqy.xpbarrel.barrel;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public record XpBarrelSnapshot(int experience, @NotNull UUID owner) {
}
