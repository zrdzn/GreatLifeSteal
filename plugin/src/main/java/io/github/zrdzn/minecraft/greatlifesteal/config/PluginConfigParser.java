package io.github.zrdzn.minecraft.greatlifesteal.config;

import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationMode;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationModeAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.stream.Collectors;

public class PluginConfigParser {

    public PluginConfig parse(ConfigurationSection section) throws InvalidConfigurationException {
        if (section == null) {
            throw new InvalidConfigurationException("Configuration section cannot be null.");
        }

        int defaultHealth = section.getInt("defaultHealth");
        if (defaultHealth < 1) {
            throw new InvalidConfigurationException("Property 'defaultHealth' cannot be lower than 1.");
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

        ConfigurationSection heartItemSection = section.getConfigurationSection("heartItem");
        if (heartItemSection == null) {
            throw new InvalidConfigurationException("Section 'heartItem' cannot be null.");
        }

        HeartItem heartItem = null;
        if (heartItemSection.getBoolean("enabled")) {
            int heartItemHealthAmount = heartItemSection.getInt("healthAmount");
            if (heartItemHealthAmount < 0) {
                throw new InvalidConfigurationException("Property 'healthAmount' cannot be lower than 0.");
            }

            Material heartItemType = Material.matchMaterial("type");
            if (heartItemType == null) {
                throw new InvalidConfigurationException("Property 'type' is not a valid item type.");
            }

            ItemStack heartItemStack = new ItemStack(heartItemType);

            ConfigurationSection heartItemMetaSection = heartItemSection.getConfigurationSection("meta");
            if (heartItemMetaSection != null) {
                ItemMeta heartItemMeta = heartItemStack.getItemMeta();

                String displayName = heartItemSection.getString("meta.displayName");
                if (displayName == null) {
                    throw new InvalidConfigurationException("Property 'displayName' cannot be null.");
                }

                heartItemMeta.setDisplayName(formatColor(displayName));
                heartItemMeta.setLore(formatColor(heartItemMetaSection.getStringList("lore")));

                heartItemStack.setItemMeta(heartItemMeta);
            }

            ShapedRecipe heartItemRecipe = new ShapedRecipe(heartItemStack.clone());

            heartItemRecipe.shape("123", "456", "789");

            ConfigurationSection heartItemRecipeSection = heartItemSection.getConfigurationSection("craftingRecipe");
            if (heartItemRecipeSection == null) {
                throw new InvalidConfigurationException("Section 'craftingRecipe' cannot be null.");
            }

            for (String key : heartItemRecipeSection.getKeys(false)) {
                Material material = Material.matchMaterial(heartItemRecipeSection.getString(key));
                if (material == null) {
                    throw new InvalidConfigurationException("Item type in the recipe section is invalid.");
                }

                heartItemRecipe.setIngredient(key.charAt(0), material);
            }

            heartItem = new HeartItem(heartItemHealthAmount, heartItemRecipe);
        }
        
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

        return new PluginConfig(defaultHealth, healthChange, new SimpleImmutableEntry<>(minimumHealth, maximumHealth),
            killByPlayerOnly, heartItem, elimination);
    }

    private static String formatColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private static List<String> formatColor(List<String> strings) {
        return strings.stream()
            .map(PluginConfigParser::formatColor)
            .collect(Collectors.toList());
    }

}
