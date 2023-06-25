package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class V1_14R1NbtService implements NbtService {

    private final Plugin plugin;

    public V1_14R1NbtService(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setBoolean(ItemStack itemStack, String key, boolean value) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, key);

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, value ? 1 : 0);

        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public boolean getBoolean(ItemStack itemStack, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, key);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            throw new IllegalArgumentException("Item meta cannot be null");
        }

        Object value = itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.INTEGER);
        if (value == null) {
            throw new IllegalArgumentException("Item meta does not contain the key: " + key);
        }

        return (int) value == 1;
    }

    @Override
    public void setDouble(ItemStack itemStack, String key, double value) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, key);

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.DOUBLE, value);

        itemStack.setItemMeta(itemMeta);
    }

    @Override
    public double getDouble(ItemStack itemStack, String key) {
        NamespacedKey namespacedKey = new NamespacedKey(this.plugin, key);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            throw new IllegalArgumentException("Item meta cannot be null");
        }

        Object value = itemMeta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.DOUBLE);
        if (value == null) {
            throw new IllegalArgumentException("Item meta does not contain the key: " + key);
        }

        return (double) value;
    }

}
