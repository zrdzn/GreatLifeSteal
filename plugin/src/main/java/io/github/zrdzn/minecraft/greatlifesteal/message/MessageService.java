package io.github.zrdzn.minecraft.greatlifesteal.message;

import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

public class MessageService {

    public static CompletableFuture<Void> send(CommandSender receiver, final String message, String... placeholders) {
        return CompletableFuture.runAsync(() -> {
            if (!message.isEmpty()) {
                receiver.sendMessage(GreatLifeStealPlugin.formatColor(formatPlaceholders(message, placeholders)));
            }
        });
    }

    public static String formatPlaceholders(String message, String... placeholders) {
        int length = placeholders.length;
        if (length <= 0 || length % 2 != 0) {
            return message;
        }

        for (int index = 0; index < length; index += 2) {
            message = StringUtils.replace(message, placeholders[index], placeholders[index + 1]);
        }

        return message;
    }

}
