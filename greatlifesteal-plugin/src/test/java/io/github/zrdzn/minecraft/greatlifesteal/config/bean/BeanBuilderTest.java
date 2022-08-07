package io.github.zrdzn.minecraft.greatlifesteal.config.bean;

import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.ExampleBean;
import io.github.zrdzn.minecraft.greatlifesteal.config.bean.beans.ExampleSecondBean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeanBuilderTest {

    @Test
    public void shouldReturnValidClassType() {
        Class<?> beanClass = BeanBuilder.from(ExampleBean.class).build().getClass();

        assertEquals(ExampleBean.class, beanClass);
        assertNotEquals(ExampleSecondBean.class, beanClass);
    }

    @Test
    public void shouldReturnDefaultValues() {
        ExampleBean bean = BeanBuilder.from(ExampleBean.class).build();

        assertEquals(0, bean.getIdentifier());
        assertNotEquals(5, bean.getIdentifier());

        assertTrue(bean.isEnabled());

        assertEquals("Default name", bean.getName());
        assertNotEquals("Default", bean.getName());
    }

    @Test
    public void shouldReturnModifiedValues() {
        ExampleBean modifiedBean = BeanBuilder
                .from(ExampleBean.class)
                .with(bean -> bean.setEnabled(false))
                .with(bean -> bean.setIdentifier(1))
                .with(bean -> bean.setName("Modified name"))
                .build();

        assertEquals(1, modifiedBean.getIdentifier());
        assertNotEquals(0, modifiedBean.getIdentifier());

        assertFalse(modifiedBean.isEnabled());

        assertEquals("Modified name", modifiedBean.getName());
        assertNotEquals("Default name", modifiedBean.getName());
    }

}
