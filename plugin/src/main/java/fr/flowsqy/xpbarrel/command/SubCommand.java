package fr.flowsqy.xpbarrel.command;

import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.chat.BaseComponent;

public class SubCommand {

    private final String name;
    private final String[] aliases;
    private final String permission;
    private final BaseComponent helpMessage;
    private final Executor executor;
    private final TabExecutor tabCompleter;

    public SubCommand(@NotNull String name, @NotNull String[] aliases, @NotNull String permission,
            @Nullable BaseComponent helpMessage, @NotNull Executor executor) {
        this(name, aliases, permission, helpMessage, executor, null);
    }

    public SubCommand(@NotNull String name, @NotNull String[] aliases, @NotNull String permission,
            @Nullable BaseComponent helpMessage, @NotNull Executor executor, @Nullable TabExecutor tabCompleter) {
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.helpMessage = helpMessage;
        this.executor = executor;
        this.tabCompleter = tabCompleter;
    }

    public boolean match(String argument) {
        if (name.equalsIgnoreCase(argument)) {
            return true;
        }
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getPermission() {
        return permission;
    }

    @Nullable
    public BaseComponent getHelpMessage() {
        return helpMessage;
    }

    @NotNull
    public Executor getExecutor() {
        return executor;
    }

    @Nullable
    public TabExecutor getTabCompleter() {
        return tabCompleter;
    }

}
