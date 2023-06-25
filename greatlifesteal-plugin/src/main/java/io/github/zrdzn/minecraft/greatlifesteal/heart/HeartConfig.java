package io.github.zrdzn.minecraft.greatlifesteal.heart;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.BooleanProperty;
import ch.jalu.configme.properties.DoubleProperty;
import ch.jalu.configme.properties.EnumProperty;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyBuilder;
import ch.jalu.configme.properties.types.BeanPropertyType;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.BasicItemBean;
import java.util.Map;
import org.bukkit.Material;

/**
 * Represents 'baseSettings.heartItem' section.
 */
public class HeartConfig implements SettingsHolder {

    @Comment("If any heart item should be enabled on the server.")
    public static final Property<Boolean> ENABLED = new BooleanProperty("baseSettings.heartItem.enabled", true);

    @Comment("Amount of health points that should be given to a player on item consume.")
    public static final Property<Double> HEALTH_AMOUNT = new DoubleProperty("baseSettings.heartItem.healthAmount", 2.0D);

    @Comment("Upper maximum health limit for healing with the heart item.")
    public static final Property<Double> MAXIMUM_HEALTH_LIMIT = new DoubleProperty("baseSettings.heartItem.maximumHealthLimit", 20.0D);

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

    private HeartConfig() {
    }

    @Override
    public void registerComments(CommentsConfiguration config) {
        config.setComment(
                "baseSettings.heartItem",
                "Item that can be used by a player to give him a specified amount of health points."
        );
    }

}
