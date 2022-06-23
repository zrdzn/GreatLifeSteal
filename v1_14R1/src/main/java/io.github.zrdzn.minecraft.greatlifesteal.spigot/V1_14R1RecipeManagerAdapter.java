package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import java.lang.reflect.Field;
import java.util.Map;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.server.v1_14_R1.CraftingManager;
import net.minecraft.server.v1_14_R1.IRecipe;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.Recipes;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.inventory.ShapedRecipe;

public class V1_14R1RecipeManagerAdapter implements RecipeManagerAdapter {

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeServerShapedRecipe(ShapedRecipe recipe) {
        try {
            Field field = CraftingManager.class.getDeclaredField("recipes");
            if (!field.getType().equals(Map.class)) {
                return false;
            }

            CraftingManager craftingManager = ((CraftServer) Bukkit.getServer()).getServer().getCraftingManager();

            Map<Recipes<?>, Object2ObjectLinkedOpenHashMap<MinecraftKey, IRecipe<?>>> recipesRaw =
                    (Map<Recipes<?>, Object2ObjectLinkedOpenHashMap<MinecraftKey, IRecipe<?>>>) field.get(craftingManager);

            for (Object2ObjectLinkedOpenHashMap<MinecraftKey, IRecipe<?>> foundRecipes : recipesRaw.values()) {
                if (foundRecipes.object2ObjectEntrySet().removeIf(foundRecipe ->
                        foundRecipe.getKey().toString().equals("greatlifesteal:heartitemrecipe"))) {
                    return true;
                }
            }

            return false;
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            return false;
        }
    }

}
