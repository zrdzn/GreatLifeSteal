package io.github.zrdzn.minecraft.greatlifesteal.config.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.validator.annotation.PositiveOrZero;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;
import org.bukkit.Material;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeartItemConfig extends OkaeriConfig {

    @Comment("If any heart item should be enabled on the server.")
    public boolean enabled = true;

    @Comment("")
    @Comment("If the heart item should be given to killer, when he reaches maximumHealth.")
    public boolean rewardHeartOnOverlimit = false;

    @PositiveOrZero
    @Comment("")
    @Comment("Amount of health points that should be given to a player on item consume.")
    public int healthAmount = 2;

    @Comment("")
    @Comment("Type of the item that the heart item should be.")
    public Material type = Material.APPLE;

    @Comment("")
    @Comment("Meta for the heart item.")
    public HeartMetaConfig meta = new HeartMetaConfig();

    @Comment("")
    @Comment("Recipe for the heart item creation. Each number is an ordered slot in the workbench (1-9).")
    public Map<String, RecipeItemConfig> crafting = new HashMap<String, RecipeItemConfig>() {{
        for (int i = 1; i < 10; i++) {
            this.put(String.valueOf(i), new RecipeItemConfig());
        }
    }};

    public class HeartMetaConfig extends OkaeriConfig {

        @Comment("Display name for the item.")
        private String displayName = "&aThe Heart of an Elk";

        @Comment("")
        @Comment("Lore for the item.")
        private List<String> lore = Collections.singletonList("&aUse this item to give yourself health points.");

        public String getDisplayName() {
            return GreatLifeStealPlugin.formatColor(this.displayName);
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public List<String> getLore() {
            return GreatLifeStealPlugin.formatColor(this.lore);
        }

        public void setLore(List<String> lore) {
            this.lore = lore;
        }

    }

    public class RecipeItemConfig extends OkaeriConfig {

        public Material type = Material.DIAMOND_BLOCK;

        public Integer amount = 1;

    }

}
