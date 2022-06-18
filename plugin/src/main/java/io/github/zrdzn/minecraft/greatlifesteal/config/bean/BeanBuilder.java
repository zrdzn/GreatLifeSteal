package io.github.zrdzn.minecraft.greatlifesteal.config.bean;

import java.util.function.Consumer;

public class BeanBuilder<T> {

    private T bean;

    public BeanBuilder(Class<T> bean) {
        try {
            this.bean = bean.newInstance();
        } catch (InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    public static <T> BeanBuilder<T> from(Class<T> bean) {
        return new BeanBuilder<>(bean);
    }

    public BeanBuilder<T> with(Consumer<T> modify) {
        modify.accept(this.bean);
        return this;
    }

    public T build() {
        return this.bean;
    }

}
