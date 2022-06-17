package io.github.zrdzn.minecraft.greatlifesteal.config;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.properties.Property;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class ConfigConsistencyTest {

    private static ConfigurationData configurationData;

    @BeforeAll
    public static void buildConfigurationData() {
        configurationData = ConfigurationDataBuilder.build();
    }

    @Test
    public void shouldHaveCommentOnEachProperty() {
        List<Property<?>> properties = configurationData.getProperties();

        for (Property<?> property : properties) {
            String path = property.getPath();
            if (path.startsWith("messages")) {
                continue;
            }

            if (configurationData.getCommentsForSection(path).isEmpty()) {
                fail("No comment defined for " + property);
            }
        }
    }

}
