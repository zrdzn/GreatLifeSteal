package io.github.zrdzn.minecraft.greatlifesteal.user;

import io.github.zrdzn.minecraft.greatlifesteal.datasource.DataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {

    public static final String TABLE_NAME = "gls_users";

    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean save(UUID userId, int health) {
        String query = "INSERT INTO " + TABLE_NAME + " (user_uuid, user_health) VALUES (?, ?);";
        return this.dataSource.update(query, userId.toString(), health) > 0;
    }

    public Optional<Entry<UUID, Integer>> findByUserId(UUID userId) {
        String query = "SELECT user_health FROM " + TABLE_NAME + " WHERE user_uuid = ?;";

        Optional<ResultSet> resultMaybe = this.dataSource.query(query, userId.toString());
        if (!resultMaybe.isPresent()) {
            return Optional.empty();
        }

        ResultSet result = resultMaybe.get();

        int health;
        try {
            health = result.getInt("user_health");
        } catch (SQLException exception) {
            return Optional.empty();
        }

        return Optional.of(new AbstractMap.SimpleImmutableEntry<>(userId, health));
    }

    public void setHealthByUserId(UUID userId, int value) {
        String query = "UPDATE " + TABLE_NAME + " SET user_health = ? WHERE user_uuid = ?;";
        this.dataSource.update(query, value, userId.toString());
    }

    public void changeHealthByUserId(UUID userId, int change) {
        String query = "UPDATE " + TABLE_NAME + " SET user_health = user_health + ? WHERE user_uuid = ?;";
        this.dataSource.update(query, change, userId.toString());
    }

    public boolean deleteByUserId(UUID userId) {
        return this.dataSource.update("DELETE FROM " + TABLE_NAME + " WHERE user_uuid = ?;", userId.toString()) > 0;
    }

}
