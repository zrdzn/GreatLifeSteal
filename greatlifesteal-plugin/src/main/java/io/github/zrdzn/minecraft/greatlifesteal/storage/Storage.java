package io.github.zrdzn.minecraft.greatlifesteal.storage;

import javax.sql.DataSource;

public class Storage {

    private final DataSource dataSource;
    private final StorageType type;

    public Storage(DataSource dataSource, StorageType type) {
        this.dataSource = dataSource;
        this.type = type;
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    public StorageType getType() {
        return this.type;
    }

}
