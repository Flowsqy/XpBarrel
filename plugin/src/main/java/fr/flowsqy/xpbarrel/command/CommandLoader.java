package fr.flowsqy.xpbarrel.command;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.barrel.ItemManager;

public class CommandLoader {

    public void load(@NotNull XpBarrelPlugin plugin, @NotNull ItemManager itemManager) {
        final var xpBarrelCommand = plugin.getCommand("xpbarrel");
        xpBarrelCommand.setUsage("/<command> <player>");
        xpBarrelCommand.setExecutor(new GiveCommand(itemManager));
    }

}
