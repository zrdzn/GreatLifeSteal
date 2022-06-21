package io.github.zrdzn.minecraft.greatlifesteal.config;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.EnumProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.MapProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.StringListProperty;
import ch.jalu.configme.properties.convertresult.PropertyValue;
import ch.jalu.configme.properties.types.EnumPropertyType;
import ch.jalu.configme.properties.types.PropertyType;
import ch.jalu.configme.resource.PropertyReader;
import io.github.zrdzn.minecraft.greatlifesteal.action.ActionType;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.BeanBuilder;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.ActionBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.BasicItemBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.BaseConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.HealthChangeConfig;
import io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart.HeartConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;

public class ConfigMigrationService extends PlainMigrationService {

    @Override
    protected boolean performMigrations(PropertyReader reader, ConfigurationData configData) {
        return migrateCraftingRecipeToCrafting(reader, configData) |
                migrateEliminationModeToCustomActions(reader, configData) |
                migrateHealthChangeToHealthChangeSection(reader, configData) |
                removeTakeGiveHeartsFromPlayers(reader, configData) ||
                hasDeprecatedKeys(reader);
    }

    /**
     * Migrates the old crafting recipe section to the new crafting one.
     * All materials that were in the old list are transferred to the new
     * scheme with an amount of 1.
     *
     * @since 1.5.2
     *
     * @param reader the config reader
     * @param configData the config inmemory
     *
     * @return the state whether migration is required
     */
    private static boolean migrateCraftingRecipeToCrafting(PropertyReader reader, ConfigurationData configData) {
        String oldKey = "baseSettings.heartItem.craftingRecipe";
        if (!reader.contains(oldKey)) {
            return NO_MIGRATION_NEEDED;
        }

        PropertyType<Material> enumType = EnumPropertyType.of(Material.class);

        Map<String, BasicItemBean> newCrafting = new HashMap<>();

        // Converting the old map to the new one.
        new MapProperty<>(oldKey, Collections.emptyMap(), enumType).determineValue(reader).getValue()
                .forEach((key, material) -> newCrafting.put(key, BeanBuilder
                        .from(BasicItemBean.class)
                        .with(item -> item.setType(material))
                        .with(item -> item.setAmount(1))
                        .build()));

        configData.setValue(HeartConfig.CRAFTING, newCrafting);

        return MIGRATION_REQUIRED;
    }

    /**
     * Migrates the old elimination mode section to the new custom actions one.
     * Entries in custom actions depend on the old elimination's action.
     *
     * @since 1.5.2
     *
     * @param reader the config reader
     * @param configData the config inmemory
     *
     * @return the state whether migration is required
     */
    private static boolean migrateEliminationModeToCustomActions(PropertyReader reader, ConfigurationData configData) {
        String oldKey = "baseSettings.eliminationMode";

        Property<ActionType> oldActionProperty = new EnumProperty<>(ActionType.class, oldKey + ".action", ActionType.DISPATCH_COMMANDS);
        if (!oldActionProperty.isValidInResource(reader)) {
            return NO_MIGRATION_NEEDED;
        }

        ActionType oldActionType = oldActionProperty.determineValue(reader).getValue();

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
        if (oldActionType == ActionType.SPECTATOR_MODE) {
            newActions.put("spectate", beanBuilder
                    .with(spectate -> spectate.setType(ActionType.DISPATCH_COMMANDS))
                    .with(spectate -> spectate.setParameters(Collections.singletonList("gamemode spectator {victim}")))
                    .build());
        } else if (oldActionType == ActionType.BROADCAST) {
            newActions.put("announce", beanBuilder
                    .with(spectate -> spectate.setType(oldActionType))
                    .with(spectate -> spectate.setParameters(oldMessages))
                    .build());
        } else if (oldActionType == ActionType.DISPATCH_COMMANDS) {
            newActions.put("eliminate", beanBuilder
                    .with(eliminate -> eliminate.setType(oldActionType))
                    .with(eliminate -> eliminate.setParameters(oldCommands))
                    .build());
        }

        configData.setValue(BaseConfig.CUSTOM_ACTIONS, newActions);

        return MIGRATION_REQUIRED;
    }

    /**
     * Migrates the old health change integer to section and splits its
     * value to victim's health change and killer's health change.
     *
     * @since 1.6
     *
     * @param reader the config reader
     * @param configData the config inmemory
     *
     * @return the state whether migration is required
     */
    private static boolean migrateHealthChangeToHealthChangeSection(PropertyReader reader, ConfigurationData configData) {
        String oldKey = "baseSettings.healthChange";
        PropertyValue<Integer> healthProperty = new IntegerProperty(oldKey, 0).determineValue(reader);
        if (!healthProperty.isValidInResource()) {
            return NO_MIGRATION_NEEDED;
        }

        int health = healthProperty.getValue();

        configData.setValue(HealthChangeConfig.VICTIM, (double) health);
        configData.setValue(HealthChangeConfig.KILLER, (double) health);

        return MIGRATION_REQUIRED;
    }

    /**
     * Removes the old take/give hearts from a victim/killer and
     * replaces it with their healthChange set to 0.
     *
     * @since 1.6
     *
     * @param reader the config reader
     * @param configData the config inmemory
     *
     * @return the state whether migration is required
     */
    private static boolean removeTakeGiveHeartsFromPlayers(PropertyReader reader, ConfigurationData configData) {
        String oldVictimKey = "baseSettings.takeHealthFromVictim";
        String oldKillerKey = "baseSettings.giveHealthToKiller";

        PropertyValue<Boolean> oldVictimProperty = new BooleanProperty(oldVictimKey, true).determineValue(reader);
        PropertyValue<Boolean> oldKillerProperty = new BooleanProperty(oldKillerKey, true).determineValue(reader);

        if (!oldVictimProperty.isValidInResource() && !oldKillerProperty.isValidInResource()) {
            return NO_MIGRATION_NEEDED;
        }

        if (oldVictimProperty.getValue()) {
            configData.setValue(HealthChangeConfig.VICTIM, 0.0D);
        }

        if (oldKillerProperty.getValue()) {
            configData.setValue(HealthChangeConfig.KILLER, 0.0D);
        }

        return MIGRATION_REQUIRED;
    }

    private static boolean hasDeprecatedKeys(PropertyReader reader) {
        List<String> deprecatedKeys = new ArrayList<String>() {
            {
                this.add("baseSettings.heartItem.craftingRecipe");
                this.add("baseSettings.eliminationMode");
                this.add("baseSettings.healthChange");
                this.add("baseSettings.takeHealthFromVictim");
                this.add("baseSettings.giveHealthToKiller");
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
