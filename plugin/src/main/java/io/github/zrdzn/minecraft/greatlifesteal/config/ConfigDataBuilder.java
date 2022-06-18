package io.github.zrdzn.minecraft.greatlifesteal.config;

import ch.jalu.configme.configurationdata.ConfigurationData;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.StealCooldownConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartMetaConfig;

public class ConfigDataBuilder {

    public static ConfigurationData build() {
        return ch.jalu.configme.configurationdata.ConfigurationDataBuilder.createConfiguration(
                 BaseConfig.class,     MessagesConfig.class, StealCooldownConfig.class,
                HeartConfig.class,    HeartMetaConfig.class
        );
    }

}
