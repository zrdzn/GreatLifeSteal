package io.github.zrdzn.minecraft.greatlifesteal.config;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.EnumProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringListProperty;
import ch.jalu.configme.resource.PropertyReader;
import io.github.zrdzn.minecraft.greatlifesteal.action.Action;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.BeanBuilder;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.ActionBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigMigrationService extends PlainMigrationService {

    @Override
    protected boolean performMigrations(PropertyReader reader, ConfigurationData configData) {
        return migrateEliminationModeToCustomActions(reader, configData) || hasDeprecatedKeys(reader);
    }

    private static boolean migrateEliminationModeToCustomActions(PropertyReader reader, ConfigurationData configData) {
        String oldKey = "baseSettings.eliminationMode";

        Property<Action> oldActionProperty = new EnumProperty<>(Action.class, oldKey + ".action", Action.DISPATCH_COMMANDS);
        if (!oldActionProperty.isValidInResource(reader)) {
            return false;
        }

        Action oldAction = oldActionProperty.determineValue(reader).getValue();

        boolean oldEnabled = new BooleanProperty(oldKey + ".enabled", false).determineValue(reader).getValue();

        int oldRequiredHealth = new IntegerProperty(oldKey + ".requiredHealth", 4).determineValue(reader).getValue();

        List<String> oldCommands = new StringListProperty(oldKey + ".commands").determineValue(reader).getValue();
        List<String> oldMessages = new StringListProperty(oldKey + ".broadcastMessages").determineValue(reader).getValue();

        Map<String, ActionBean> newActions = new HashMap<>();

        // Set shared bean properties for all actions.
        BeanBuilder<ActionBean> beanBuilder = BeanBuilder
                .from(ActionBean.class)
                .with(bean -> bean.setEnabled(oldEnabled))
                .with(bean -> bean.setActivateAtHealth(oldRequiredHealth));

        // Initiate map with an entry that is based on the provided action.
        if (oldAction == Action.SPECTATOR_MODE) {
            newActions.put("spectate", beanBuilder
                    .with(spectate -> spectate.setType(Action.DISPATCH_COMMANDS))
                    .with(spectate -> spectate.setParameters(Collections.singletonList("gamemode spectator {victim}")))
                    .build());
        } else if (oldAction == Action.BROADCAST) {
            newActions.put("announce", beanBuilder
                    .with(spectate -> spectate.setType(oldAction))
                    .with(spectate -> spectate.setParameters(oldMessages))
                    .build());
        } else if (oldAction == Action.DISPATCH_COMMANDS) {
            newActions.put("eliminate", beanBuilder
                    .with(eliminate -> eliminate.setType(oldAction))
                    .with(eliminate -> eliminate.setParameters(oldCommands))
                    .build());
        }

        configData.setValue(BaseConfig.CUSTOM_ACTIONS, newActions);

        return true;
    }

    private static boolean hasDeprecatedKeys(PropertyReader reader) {
        List<String> deprecatedKeys = new ArrayList<String>() {
            {
                this.add("baseSettings.eliminationMode");
            }
        };

        for (String deprecatedKey : deprecatedKeys) {
            if (reader.contains(deprecatedKey)) {
                return MIGRATION_REQUIRED;
            }
        }

        return NO_MIGRATION_NEEDED;
    }

}
