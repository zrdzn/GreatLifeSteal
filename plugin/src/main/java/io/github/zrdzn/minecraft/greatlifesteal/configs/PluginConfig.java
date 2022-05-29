package io.github.zrdzn.minecraft.greatlifesteal.configs;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import io.github.zrdzn.minecraft.greatlifesteal.GreatLifeStealPlugin;

import java.util.HashMap;
import java.util.Map;

public class PluginConfig extends OkaeriConfig {

    @Comment("A section for the plugin operation.")
    @Comment("1 heart = 2 health points.")
    public BaseSettingsConfig baseSettings = new BaseSettingsConfig();

    @Comment("")
    @Comment("All messages that can be sent by the plugin to players.")
    @Comment("If you want to disable specific messages just place a '#' before them.")
    private Map<String, String> messages = new HashMap<String, String>() {{
        this.put("commandUsage", "&aType /lifesteal set/reload [player] [health_points]");
        this.put("noPermissions", "&cYou don't have enough permissions.");
        this.put("successfulCommandSet", "&aYou have successfully set &e{HEALTH} &ahp for &e{PLAYER}&a.");
        this.put("successfulCommandReload", "&aPlugin has been successfully reloaded.");
        this.put("failCommandReload", "&cCould not reload the plugin.");
        this.put("invalidPlayerProvided", "&cYou have provided invalid player.");
        this.put("invalidHealthProvided", "&cYou have provided invalid health number.");
        this.put("maxHealthReached", "&cYou have reached the maximum amount of health points.");
        this.put("pluginOutdated", "&eYou are using an outdated version of the plugin - please consider updating it on https://www.spigotmc.org/resources/greatlifesteal.102206/.");
    }};

    public Map<String, String> getMessages() {
        Map<String, String> newMessages = new HashMap<>();

        this.messages.forEach((k, v) -> newMessages.put(k, GreatLifeStealPlugin.formatColor(v)));

        return newMessages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

}
