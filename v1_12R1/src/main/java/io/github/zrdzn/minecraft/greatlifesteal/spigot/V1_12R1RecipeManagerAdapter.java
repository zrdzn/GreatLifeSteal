package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import net.minecraft.server.v1_12_R1.CraftingManager;
import net.minecraft.server.v1_12_R1.IRecipe;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.RegistryMaterials;
import org.bukkit.inventory.ShapedRecipe;

public class V1_12R1RecipeManagerAdapter implements RecipeManagerAdapter {

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeServerShapedRecipe(ShapedRecipe recipe) {
        try {
            Field field = RegistryMaterials.class.getDeclaredField("b");
            if (!field.getType().equals(Map.class)) {
                return false;
            }

            field.setAccessible(true);

            Map<IRecipe, MinecraftKey> recipes = (Map<IRecipe, MinecraftKey>) field.get(CraftingManager.recipes);

            Optional<IRecipe> recipeMaybe = recipes.entrySet().stream()
                    .filter(foundRecipe -> foundRecipe.getValue().toString().equals("greatlifesteal:heartitemrecipe"))
                    .map(Entry::getKey)
                    .findAny();

            return recipeMaybe.filter(foundRecipe -> recipes.remove(foundRecipe) != null).isPresent();
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            return false;
        }
    }

}
