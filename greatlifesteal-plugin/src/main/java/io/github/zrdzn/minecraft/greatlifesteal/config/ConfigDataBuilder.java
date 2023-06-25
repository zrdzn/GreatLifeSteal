package io.github.zrdzn.minecraft.greatlifesteal.config;

import ch.jalu.configme.configurationdata.ConfigurationData;
import io.github.zrdzn.minecraft.greatlifesteal.storage.StorageConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartDropConfig;
import io.github.zrdzn.minecraft.greatlifesteal.heart.HeartMetaConfig;

public class ConfigDataBuilder {

    public static ConfigurationData build() {
        return ch.jalu.configme.configurationdata.ConfigurationDataBuilder.createConfiguration(
                BaseConfig.class,      MessagesConfig.class,  StealCooldownConfig.class,
                HeartConfig.class,     HeartMetaConfig.class, HealthChangeConfig.class,
                HeartDropConfig.class, StorageConfig.class, DisabledWorldsConfig.class
        );
    }

}
