package io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans;

import org.bukkit.Material;

public class BasicItemBean {

    private Material type = Material.DIAMOND_BLOCK;
    private int amount = 1;

    public Material getType() {
        return this.type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

}
