package io.github.zrdzn.minecraft.greatlifesteal.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.AbstractMap.SimpleImmutableEntry;

public class PluginConfigParser {

    public PluginConfig parse(ConfigurationSection section) throws InvalidConfigurationException {
        if (section == null) {
            throw new InvalidConfigurationException("Configuration section cannot be null.");
        }

        int healthChange = section.getInt("healthChange");
        if (healthChange < 0) {
            throw new InvalidConfigurationException("Property 'healthChange' cannot be lower than 0.");
        }

        int minimumHealth = section.getInt("minimumHealth");
        if (minimumHealth < 1) {
            throw new InvalidConfigurationException("Property 'minimumHealth' cannot be lower than 1.");
        }

        int maximumHealth = section.getInt("maximumHealth");
        if (maximumHealth < minimumHealth) {
            throw new InvalidConfigurationException("Property 'maximumHealth' cannot be lower than 'minimumHealth'.");
        }

        return new PluginConfig(healthChange, new SimpleImmutableEntry<>(minimumHealth, maximumHealth));
    }

}
