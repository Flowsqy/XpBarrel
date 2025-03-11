package fr.flowsqy.xpbarrel.command;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RootCommand implements TabExecutor {

    private final SubCommand[] subCommands;
    private final Executor helpExecutor;

    public RootCommand(@NotNull SubCommand[] subCommands, @NotNull Executor helpExecutor) {
        this.subCommands = subCommands;
        this.helpExecutor = helpExecutor;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String @NotNull [] args) {
        if (args.length == 0) {
            helpExecutor.execute(sender, args);
            return true;
        }
        final String argument = args[0];
        for (SubCommand subCommand : subCommands) {
            if (!subCommand.match(argument)) {
                continue;
            }
            if (sender.hasPermission(subCommand.getPermission())) {
                subCommand.getExecutor().execute(sender, args);
                return true;
            }
            break;
        }
        helpExecutor.execute(sender, args);
        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String @NotNull [] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            final List<String> completions = new LinkedList<>();
            final String arg = args[0].toLowerCase(Locale.ENGLISH);
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().startsWith(arg) && sender.hasPermission(subCommand.getPermission())) {
                    completions.add(subCommand.getName());
                }
            }
            return completions;
        }
        final String argument = args[0];
        for (SubCommand subCommand : subCommands) {
            if (!subCommand.match(argument)) {
                continue;
            }
            if (!sender.hasPermission(subCommand.getPermission())) {
                break;
            }
            final TabCompleter tabCompleter = subCommand.getTabCompleter();
            if (tabCompleter == null) {
                return Collections.emptyList();
            }
            return tabCompleter.onTabComplete(sender, command, label, args);
        }
        return Collections.emptyList();
    }

}
