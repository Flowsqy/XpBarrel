package fr.flowsqy.xpbarrel.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface Executor {

    void execute(@NotNull CommandSender sender, @NotNull String[] args);

}
