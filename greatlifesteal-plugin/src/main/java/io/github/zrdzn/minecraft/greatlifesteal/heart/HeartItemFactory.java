package io.github.zrdzn.minecraft.greatlifesteal.heart;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import ch.jalu.configme.SettingsManager;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.BasicItemBean;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartItemFactory {

    private final Logger logger = LoggerFactory.getLogger(HeartItemFactory.class);

    private final SettingsManager config;
    private final SpigotServer spigotServer;

    public HeartItemFactory(SettingsManager config, SpigotServer spigotServer) {
        this.config = config;
        this.spigotServer = spigotServer;
    }

    public HeartItem createHeartItem() {
        ItemStack itemStack = new HeartItemStackFactory(this.config, this.spigotServer).createHeartItemStack();

        // Creating heart recipe.
        ShapedRecipe recipe = this.spigotServer.getShapedRecipeAdapter().createShapedRecipe(itemStack);
        recipe.shape("123", "456", "789");

        // Loading crafting ingredients from config.
        Map<Character, ItemStack> ingredients = this.loadCraftingIngredientsFromConfig();

        // Setting recipe ingredients.
        ingredients.forEach((slot, ingredient) -> recipe.setIngredient(slot, ingredient.getType()));

        return new HeartItem(itemStack, recipe);
    }

    private Map<Character, ItemStack> loadCraftingIngredientsFromConfig() {
        Map<Character, ItemStack> ingredients = new HashMap<>();

        for (Entry<String, BasicItemBean> item : this.config.getProperty(HeartConfig.CRAFTING).entrySet()) {
            String slotRaw = item.getKey();

            int slot;
            try {
                slot = Integer.parseUnsignedInt(slotRaw);
            } catch (NumberFormatException exception) {
                this.logger.warn("Could not parse the {} slot, because it is not a positive integer.", slotRaw);
                continue;
            }

            BasicItemBean recipeItem = item.getValue();

            ingredients.put((char) (slot + '0'), new ItemStack(recipeItem.getType(), recipeItem.getAmount()));
        }

        return ingredients;
    }

}
