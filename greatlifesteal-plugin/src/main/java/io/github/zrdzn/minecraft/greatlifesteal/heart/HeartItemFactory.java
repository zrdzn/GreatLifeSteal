package io.github.zrdzn.minecraft.greatlifesteal.heart;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import ch.jalu.configme.SettingsManager;
import dev.piotrulla.craftinglib.Crafting;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.BasicItemBean;
import io.github.zrdzn.minecraft.greatlifesteal.spigot.SpigotServer;
import org.bukkit.inventory.ItemStack;

public class HeartItemFactory {

    private final static String INGREDIENTS_MAPPING = "ABCDEFGHI";

    private final SettingsManager config;
    private final SpigotServer spigotServer;

    public HeartItemFactory(SettingsManager config, SpigotServer spigotServer) {
        this.config = config;
        this.spigotServer = spigotServer;
    }

    public HeartItem createHeartItem() {
        ItemStack itemStack = new HeartItemStackFactory(this.config, this.spigotServer).createHeartItemStack();

        // Loading crafting ingredients from config.
        Map<Character, ItemStack> ingredients = this.loadCraftingIngredientsFromConfig();

        Crafting.Builder craftingRecipe = Crafting.builder()
                .withResultItem(itemStack);

        ingredients.forEach((slot, ingredient) -> craftingRecipe.withItem(INGREDIENTS_MAPPING.indexOf(slot.toString()) + 1, ingredient));

        return new HeartItem(itemStack, craftingRecipe.build());
    }

    private Map<Character, ItemStack> loadCraftingIngredientsFromConfig() {
        Map<Character, ItemStack> ingredients = new HashMap<>();

        for (Entry<String, BasicItemBean> item : this.config.getProperty(HeartConfig.CRAFTING).entrySet()) {
            BasicItemBean recipeItem = item.getValue();

            ingredients.put(item.getKey().charAt(0), new ItemStack(recipeItem.getType(), recipeItem.getAmount()));
        }

        return ingredients;
    }

}
