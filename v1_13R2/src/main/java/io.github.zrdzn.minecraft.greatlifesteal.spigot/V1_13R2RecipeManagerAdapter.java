package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.server.v1_13_R2.CraftingManager;
import net.minecraft.server.v1_13_R2.IRecipe;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.inventory.ShapedRecipe;

public class V1_13R2RecipeManagerAdapter implements RecipeManagerAdapter {

    @SuppressWarnings("unchecked")
    @Override
    public boolean removeServerShapedRecipe(ShapedRecipe recipe) {
        try {
            Field field = CraftingManager.class.getDeclaredField("recipes");
            if (!field.getType().equals(Map.class)) {
                return false;
            }

            CraftingManager craftingManager = ((CraftServer) Bukkit.getServer()).getServer().getCraftingManager();

            Object2ObjectLinkedOpenHashMap<MinecraftKey, IRecipe> recipes =
                    (Object2ObjectLinkedOpenHashMap<MinecraftKey, IRecipe>) field.get(craftingManager);

            return recipes.object2ObjectEntrySet().removeIf(foundRecipe ->
                    foundRecipe.getKey().toString().equals("greatlifesteal:heartitemrecipe"));
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
            return false;
        }
    }

}
