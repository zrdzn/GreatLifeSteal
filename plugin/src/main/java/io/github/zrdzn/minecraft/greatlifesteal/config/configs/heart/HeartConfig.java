package io.github.zrdzn.minecraft.greatlifesteal.config.configs.heart;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.EnumProperty;
import ch.jalu.configme.properties.IntegerProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyBuilder;
import ch.jalu.configme.properties.types.BeanPropertyType;
import io.github.zrdzn.minecraft.greatlifesteal.config.beans.BasicItemBean;
import org.bukkit.Material;

import java.util.Map;

/**
 * Represents 'baseSettings.heartItem' section.
 */
public class HeartConfig implements SettingsHolder {

    @Comment("If any heart item should be enabled on the server.")
    public static final Property<Boolean> ENABLED = new BooleanProperty("baseSettings.heartItem.enabled", true);

    @Comment("If the heart item should be given to killer, when he reaches maximumHealth.")
    public static final Property<Boolean> REWARD_HEART_ON_OVERLIMIT = new BooleanProperty(
        "baseSettings.heartItem.rewardHeartOnOverlimit",
        false
    );

    @Comment("Amount of health points that should be given to a player on item consume.")
    public static final Property<Integer> HEALTH_AMOUNT = new IntegerProperty("baseSettings.heartItem.healthAmount", 2);

    @Comment("Type of the item that the heart item should be.")
    public static final Property<Material> TYPE = new EnumProperty<>(
        Material.class, "baseSettings.heartItem.type",
        Material.APPLE
    );

    @Comment("Recipe for the heart item creation. Each number is an ordered slot in the workbench (1-9).")
    public static final Property<Map<String, BasicItemBean>> CRAFTING = new PropertyBuilder
        .MapPropertyBuilder<>(BeanPropertyType.of(BasicItemBean.class))
        .path("baseSettings.heartItem.crafting")
        .defaultEntry("1", new BasicItemBean())
        .defaultEntry("2", new BasicItemBean())
        .defaultEntry("3", new BasicItemBean())
        .defaultEntry("4", new BasicItemBean())
        .defaultEntry("5", new BasicItemBean())
        .defaultEntry("6", new BasicItemBean())
        .defaultEntry("7", new BasicItemBean())
        .defaultEntry("8", new BasicItemBean())
        .defaultEntry("9", new BasicItemBean())
        .build();

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
            "baseSettings.heartItem",
            "Item that can be used by a player to give him a specified amount of health points.");
    }

}
