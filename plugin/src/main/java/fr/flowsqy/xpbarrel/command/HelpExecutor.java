package fr.flowsqy.xpbarrel.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HelpExecutor implements Executor {

    private final SubCommand[] subCommands;

    public HelpExecutor(@NotNull SubCommand[] subCommands) {
        this.subCommands = subCommands;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        for (var subCommand : subCommands) {
            if (!sender.hasPermission(subCommand.getPermission())) {
                continue;
            }
            final var helpMessage = subCommand.getHelpMessage();
            if (helpMessage == null) {
                continue;
            }
            sender.spigot().sendMessage(helpMessage);
        }
    }

}
