package io.github.zrdzn.minecraft.greatlifesteal.message;

import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

public class MessageService {

    public static CompletableFuture<Void> send(CommandSender receiver, final String message, String... placeholders) {
        return CompletableFuture.runAsync(() -> {
            int length = placeholders.length;
            if (length <= 0 || length % 2 != 0) {
                receiver.sendMessage(GreatLifeStealPlugin.formatColor(message));
                return;
            }

            String formattedMessage = message;
            for (int index = 0; index < length; index += 2) {
                formattedMessage = StringUtils.replace(formattedMessage, placeholders[index], placeholders[index + 1]);
            }

            receiver.sendMessage(GreatLifeStealPlugin.formatColor(formattedMessage));
        });
    }

}
