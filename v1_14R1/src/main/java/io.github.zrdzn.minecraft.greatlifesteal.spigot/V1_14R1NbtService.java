package io.github.zrdzn.minecraft.greatlifesteal.spigot;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class V1_14R1NbtService implements NbtService {

    private final NamespacedKey namespacedKey;

    public V1_14R1NbtService(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    @Override
    public void applyHeartData(ItemStack heartItem) {
        ItemMeta heartMeta = heartItem.getItemMeta();
        if (heartMeta == null) {
            return;
        }

        heartMeta.getPersistentDataContainer().set(this.namespacedKey, PersistentDataType.INTEGER, 1);
    }

}
