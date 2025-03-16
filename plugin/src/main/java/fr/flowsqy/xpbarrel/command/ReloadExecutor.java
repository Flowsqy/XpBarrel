package fr.flowsqy.xpbarrel.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.config.ConfigLoader;
import fr.flowsqy.xpbarrel.config.MessageConfig;
import fr.flowsqy.xpbarrel.load.PluginData;
import fr.flowsqy.xpbarrel.load.PluginDataLoader;
import net.md_5.bungee.api.chat.BaseComponent;

public class ReloadExecutor implements Executor {

    private final PluginData pluginData;
    private final BaseComponent failMessage, successMessage;

    public ReloadExecutor(@NotNull PluginData pluginData, @NotNull MessageConfig messageConfig) {
        this.pluginData = pluginData;
        failMessage = messageConfig.getComponentMessage("command.reload.fail");
        successMessage = messageConfig.getComponentMessage("command.reload.success");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        final var configLoader = new ConfigLoader();
        if (!configLoader.checkDataFolder(pluginData.plugin().getDataFolder())) {
            if (failMessage == null) {
                return;
            }
            sender.spigot().sendMessage(failMessage);
            return;
        }
        final var pluginDataLoader = new PluginDataLoader();
        pluginDataLoader.save(pluginData);
        pluginDataLoader.load(pluginData);
        if (successMessage == null) {
            return;
        }
        sender.spigot().sendMessage(successMessage);
    }

}
