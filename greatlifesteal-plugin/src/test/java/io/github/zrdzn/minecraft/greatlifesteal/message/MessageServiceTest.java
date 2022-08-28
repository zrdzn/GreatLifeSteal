package io.github.zrdzn.minecraft.greatlifesteal.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

    @Test
    public void shouldFormatPlaceholdersInCorrectOrder() {
        String message = "This is {name}, she has been working for {years} years now.";
        String correctMessage = "This is Anna, she has been working for 5 years now.";

        String[] correctOrder = {
                "{name}", "Anna",
                "{years}", "5"
        };

        String[] incorrectOrder = {
                "Anna", "{name}",
                "5", "{years}"
        };

        assertEquals(correctMessage, MessageService.formatPlaceholders(message, correctOrder));
        assertNotEquals(correctMessage, MessageService.formatPlaceholders(message, incorrectOrder));
    }

    @Test
    public void shouldNotFormatPlaceholderWithoutValue() {
        String message = "Placeholder {id} should not be replaced.";

        assertEquals(message, MessageService.formatPlaceholders(message, "{id}"));
    }

}
