package io.github.zrdzn.minecraft.greatlifesteal.heart;

import java.util.Collections;
import java.util.List;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class HeartMetaConfig extends OkaeriConfig {

    @Comment("Display name for the heart item.")
    private String displayName = "&aThe Healing Heart";

    @Comment("Lore for the heart item.")
    private List<String> lore = Collections.singletonList("&aUse this item to increase your maximum health.");

    @Comment("Should the heart item glow?")
    private boolean glowing = false;

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

    public boolean isGlowing() {
        return this.glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

}
