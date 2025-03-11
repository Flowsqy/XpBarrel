package fr.flowsqy.xpbarrel.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NameTabCompleter implements TabCompleter {

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender,
            @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length == 2) {
            return null;
        }
        return Collections.emptyList();
    }

}
