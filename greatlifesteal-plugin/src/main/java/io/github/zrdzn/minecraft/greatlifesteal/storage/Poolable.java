package io.github.zrdzn.minecraft.greatlifesteal.storage;

import com.zaxxer.hikari.HikariDataSource;

public interface Poolable {

    HikariDataSource getHikariDataSource();

}
