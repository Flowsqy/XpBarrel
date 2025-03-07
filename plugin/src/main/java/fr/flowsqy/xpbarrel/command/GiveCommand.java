package fr.flowsqy.xpbarrel.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.barrel.ItemManager;

public class GiveCommand implements CommandExecutor {

    private final ItemManager itemManager;

    public GiveCommand(@NotNull ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            String @NotNull [] args) {
        if (args.length != 1) {
            return false;
        }
        final var targetPlayer = Bukkit.getPlayerExact(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(args[0] + " is not a connected player");
            return true;
        }
        targetPlayer.getInventory().addItem(itemManager.generateItem(targetPlayer.getUniqueId(), 0));
        return true;
    }

}
