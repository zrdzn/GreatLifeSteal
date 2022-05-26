package io.github.zrdzn.minecraft.greatlifesteal.message;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class MessageLoader {

    public static final String DEFAULT_MESSAGE = "<message not found>";

    private final MessageCache cache;

    public MessageLoader(MessageCache cache) {
        this.cache = cache;
    }

    public void load(ConfigurationSection section) throws InvalidConfigurationException {
        if (section == null) {
            throw new InvalidConfigurationException("Provided section is null.");
        }

        if (!section.getName().equals("messages")) {
            throw new InvalidConfigurationException("Provided section is not 'messages'.");
        }

        section.getKeys(false).forEach(key ->
            this.cache.addMessage(key, formatColor(section.getString(key))));
    }

    private static String formatColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
