package fr.flowsqy.xpbarrel.command;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.XpBarrelPlugin;
import fr.flowsqy.xpbarrel.barrel.ItemManager;
import fr.flowsqy.xpbarrel.config.MessageConfig;

public class CommandLoader {

    public void load(@NotNull XpBarrelPlugin plugin, @NotNull MessageConfig messageConfig,
            @NotNull ItemManager itemManager) {
        final var xpBarrelCommand = Objects.requireNonNull(plugin.getCommand("xpbarrel"));
        final SubCommand[] subCommands = new SubCommand[1];
        final var helpExecutor = new HelpExecutor(subCommands);
        subCommands[0] = new SubCommand("help", new String[] { "h" }, "xpbarrel.command.help",
                messageConfig.getComponentMessage("command.help.help"), helpExecutor);
        final var rootCommandExecutor = new RootCommand(subCommands, helpExecutor);
        xpBarrelCommand.setExecutor(rootCommandExecutor);
        xpBarrelCommand.setTabCompleter(rootCommandExecutor);
    }

}
