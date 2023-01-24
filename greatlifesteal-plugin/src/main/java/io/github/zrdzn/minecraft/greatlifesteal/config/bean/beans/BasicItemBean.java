package io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

public class BasicItemBean {

    private Material type = Material.DIAMOND_BLOCK;
    private int amount = 1;
    private String displayName = null;
    private List<String> lore = new ArrayList<>();

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

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

}
