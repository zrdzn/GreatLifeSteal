package io.github.zrdzn.minecraft.greatlifesteal.config.bean;

import java.util.function.Consumer;

public class BeanBuilder<T> {

    private T bean;

    private BeanBuilder(Class<T> bean) {
        try {
            this.bean = bean.newInstance();
        } catch (InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Define what is the bean class you want to work on.
     *
     * @param bean the class of the bean
     * @param <T>  the bean object
     *
     * @return the new builder holding the bean instance
     */
    public static <T> BeanBuilder<T> from(Class<T> bean) {
        return new BeanBuilder<>(bean);
    }

    /**
     * Modify the bean and return the builder with it.
     *
     * @param modify the consumer for the bean
     *
     * @return the builder with the modified bean
     */
    public BeanBuilder<T> with(Consumer<T> modify) {
        modify.accept(this.bean);
        return this;
    }

    /**
     * Gets the final bean.
     *
     * @return the bean
     */
    public T build() {
        return this.bean;
    }

}
