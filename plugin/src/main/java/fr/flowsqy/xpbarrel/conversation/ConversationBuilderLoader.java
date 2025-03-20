package fr.flowsqy.xpbarrel.conversation;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import fr.flowsqy.xpbarrel.config.Config;

public class ConversationBuilderLoader {

    @NotNull
    public ConversationBuilder load(@NotNull Plugin plugin, @NotNull Config config) {
        return new ConversationBuilder(plugin, config.getConversationCancelWords(), config.getConversationTimeout());
    }

}
