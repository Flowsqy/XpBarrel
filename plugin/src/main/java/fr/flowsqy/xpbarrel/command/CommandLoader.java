package fr.flowsqy.xpbarrel.command;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.config.MessageConfig;
import fr.flowsqy.xpbarrel.load.PluginData;

public class CommandLoader {

    public void load(@NotNull PluginData pluginData, @NotNull MessageConfig messageConfig) {
        final var xpBarrelCommand = Objects.requireNonNull(pluginData.plugin().getCommand("xpbarrel"));
        final SubCommand[] subCommands = new SubCommand[3];
        final var helpExecutor = new HelpExecutor(subCommands);
        subCommands[0] = new SubCommand("help", new String[] { "h" }, "xpbarrel.command.help",
                messageConfig.getComponentMessage("command.help.help"), helpExecutor);
        subCommands[1] = new SubCommand("reload", new String[] { "rl" }, "xpbarrel.command.reload",
                messageConfig.getComponentMessage("command.help.reload"), new ReloadExecutor());
        final var giveHelpMessage = messageConfig.getComponentMessage("command.help.give");
        subCommands[2] = new SubCommand("give", new String[] { "g" }, "xpbarrel.command.give", giveHelpMessage,
                new GiveExecutor(pluginData.itemManager(), giveHelpMessage, messageConfig), new NameTabCompleter());
        final var rootCommandExecutor = new RootCommand(subCommands, helpExecutor);
        xpBarrelCommand.setExecutor(rootCommandExecutor);
        xpBarrelCommand.setTabCompleter(rootCommandExecutor);
    }

}
