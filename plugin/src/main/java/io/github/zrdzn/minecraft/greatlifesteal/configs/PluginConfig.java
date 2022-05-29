package io.github.zrdzn.minecraft.greatlifesteal.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public class PluginConfig extends OkaeriConfig {

    @Comment("A section for the plugin operation.")
    @Comment("1 heart = 2 health points.")
    public BaseSettingsConfig baseSettings = new BaseSettingsConfig();

    @Comment("")
    @Comment("All messages that can be sent by the plugin to players.")
    @Comment("If you want to disable specific messages just place a '#' before them.")
    public MessagesConfig messages = new MessagesConfig();

}
