package io.github.zrdzn.minecraft.greatlifesteal.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MessageServiceTest {

    @Test
    public void shouldFormatPlaceholders() {
        String message = "Hello! My name is {name} and I am {age} years old!";

        String[] placeholders = {
            "{name}", "John",
            "{age}", "20"
        };

        String expectedMessage = "Hello! My name is John and I am 20 years old!";

        assertEquals(expectedMessage, MessageService.formatPlaceholders(message, placeholders));
    }

}
