package io.github.zrdzn.minecraft.greatlifesteal.config;

import ch.jalu.configme.configurationdata.ConfigurationData;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.storage.StorageConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.DisabledWorldsConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.HealthChangeConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.MessagesConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.StealCooldownConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.configs.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.configs.HeartDropConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.configs.HeartMetaConfig;

public class ConfigDataBuilder {

    public static ConfigurationData build() {
        return ch.jalu.configme.configurationdata.ConfigurationDataBuilder.createConfiguration(
                BaseConfig.class,      MessagesConfig.class,  StealCooldownConfig.class,
                HeartConfig.class,     HeartMetaConfig.class, HealthChangeConfig.class,
                HeartDropConfig.class, StorageConfig.class, DisabledWorldsConfig.class
        );
    }

}
