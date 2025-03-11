package fr.flowsqy.xpbarrel.command;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.xpbarrel.barrel.ItemManager;
import fr.flowsqy.xpbarrel.config.MessageConfig;
import net.md_5.bungee.api.chat.BaseComponent;

public class GiveExecutor implements Executor {

    private final ItemManager itemManager;
    private final BaseComponent helpMessage;
    private final String noPlayerMessage;
    private final String successMessage;

    public GiveExecutor(@NotNull ItemManager itemManager, @Nullable BaseComponent helpMessage,
            @NotNull MessageConfig messageConfig) {
        this.itemManager = itemManager;
        this.helpMessage = helpMessage;
        this.noPlayerMessage = messageConfig.getMessage("command.give.not-player");
        this.successMessage = messageConfig.getMessage("command.give.success");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length > 2) {
            if (helpMessage == null) {
                return;
            }
            sender.spigot().sendMessage(helpMessage);
            return;
        }
        Player targetPlayer = null;
        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                if (helpMessage == null) {
                    return;
                }
                sender.spigot().sendMessage(helpMessage);
                return;
            }
            targetPlayer = player;
        } else if (args.length == 2) {
            targetPlayer = Bukkit.getPlayerExact(args[1]);
            if (targetPlayer == null) {
                if (noPlayerMessage == null) {
                    return;
                }
                sender.sendMessage(noPlayerMessage.replace("%player%", args[1]));
                return;
            }
        }
        Objects.requireNonNull(targetPlayer);
        final var leftItems = targetPlayer.getInventory()
                .addItem(itemManager.generateItem(targetPlayer.getUniqueId(), 0));
        final var world = targetPlayer.getWorld();
        final var location = targetPlayer.getLocation();
        for (var entry : leftItems.entrySet()) {
            world.dropItemNaturally(location, entry.getValue());
        }
        if (successMessage != null) {
            sender.sendMessage(successMessage.replace("%player%", targetPlayer.getName()));
        }
        return;
    }

}
