package io.github.zrdzn.minecraft.greatlifesteal.config;

import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationMode;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationModeAction;
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

        boolean killByPlayerOnly = section.getBoolean("killByPlayerOnly");

        ConfigurationSection eliminationSection = section.getConfigurationSection("eliminationMode");
        if (eliminationSection == null) {
            throw new InvalidConfigurationException("Section 'eliminationMode' cannot be null.");
        }

        EliminationMode elimination = null;

        boolean eliminationEnabled = eliminationSection.getBoolean("enabled");
        if (eliminationEnabled) {
            int eliminationRequiredHealth = eliminationSection.getInt("requiredHealth");
            if (eliminationRequiredHealth < 1) {
                throw new InvalidConfigurationException("Property 'requiredHealth' cannot be lower than 1.");
            }

            String eliminationActionRaw = eliminationSection.getString("action");
            if (eliminationActionRaw == null) {
                throw new InvalidConfigurationException("Property 'action' cannot be null.");
            }

            EliminationModeAction eliminationAction;
            try {
                eliminationAction = EliminationModeAction.valueOf(eliminationActionRaw);
            } catch (IllegalArgumentException exception) {
                throw new InvalidConfigurationException("Property 'action' is not a valid action.");
            }

            elimination = new EliminationMode(eliminationRequiredHealth, eliminationAction);
        }

        return new PluginConfig(healthChange, new SimpleImmutableEntry<>(minimumHealth, maximumHealth), killByPlayerOnly,
            elimination);
    }

}
