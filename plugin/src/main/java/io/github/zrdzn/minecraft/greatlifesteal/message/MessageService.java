package io.github.zrdzn.minecraft.greatlifesteal.message;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MessageService {

    private final Map<String, String> messages;

    public MessageService(Map<String, String> messages) {
        this.messages = messages;
    }

    public CompletableFuture<Void> send(CommandSender receiver, String key, String... placeholders) {
        return CompletableFuture.runAsync(() -> {
            String message = this.messages.get(key);

            int length = placeholders.length;
            if (length <= 0 || length % 2 != 0) {
                receiver.sendMessage(message);
                return;
            }

            for (int index = 0; index < length; index += 2) {
                message = StringUtils.replace(message, placeholders[index], placeholders[index + 1]);
            }

            receiver.sendMessage(message);
        });
    }

}
