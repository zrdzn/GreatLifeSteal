package io.github.zrdzn.minecraft.greatlifesteal.repository.repositories.sqlite;

import io.github.zrdzn.minecraft.greatlifesteal.storage.SqliteStorage;
import io.github.zrdzn.minecraft.greatlifesteal.repository.UserRepository;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SqliteUserRepository implements UserRepository {

    private static final String INSERT = "INSERT INTO gls_users (user_uuid, user_health) VALUES (?, ?);";
    private static final String SELECT_USER_BY_UUID = "SELECT user_health FROM gls_users WHERE user_uuid = ?;";
    private static final String SELECT_ALL = "SELECT user_uuid, user_health FROM gls_users;";
    private static final String UPDATE_HEALTH_SET_BY_UUID = "UPDATE gls_users SET user_health = ? WHERE user_uuid = ?;";
    private static final String UPDATE_HEALTH_CHANGE_BY_UUID = "UPDATE gls_users SET user_health = user_health + ? WHERE user_uuid = ?;";
    private static final String DELETE_USER_BY_UUID = "DELETE FROM gls_users WHERE user_uuid = ?;";

    private final Logger logger;
    private final SqliteStorage storage;

    public SqliteUserRepository(Logger logger, SqliteStorage storage) {
        this.logger = logger;
        this.storage = storage;
    }

    public boolean save(UUID userId, int health) {
        if (this.findByUserId(userId).isPresent()) {
            return false;
        }

        try (
            Connection connection = this.storage.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT)
        ) {
            statement.setString(1, userId.toString());
            statement.setInt(2, health);

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            this.logger.error("Could not save the user to the database.", exception);
            return false;
        }
    }

    public Optional<Entry<UUID, Integer>> findByUserId(UUID userId) {
        try (
            Connection connection = this.storage.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_UUID)
        ) {
            statement.setString(1, userId.toString());

            ResultSet result = statement.executeQuery();

            int health = result.getInt("user_health");
            if (health == 0) {
                return Optional.empty();
            }

            return Optional.of(new SimpleImmutableEntry<>(userId, health));
        } catch (SQLException exception) {
            this.logger.error("Could not get the user from the database.");
            return Optional.empty();
        }
    }

    public Map<UUID, Integer> listAll() {
        try (
            Connection connection = this.storage.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL)
        ) {
            Map<UUID, Integer> users = new ConcurrentHashMap<>();

            ResultSet result = statement.executeQuery();
            while (result.next()) {
                users.put(UUID.fromString(result.getString("user_uuid")), result.getInt("user_health"));
            }

            return users;
        } catch (SQLException exception) {
            this.logger.error("Could not get users from the database.");
            return Collections.emptyMap();
        }
    }

    public boolean setHealthByUserId(UUID userId, int value) {
        if (!this.findByUserId(userId).isPresent()) {
            return false;
        }

        try (
            Connection connection = this.storage.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_HEALTH_SET_BY_UUID)
        ) {
            statement.setInt(1, value);
            statement.setString(2, userId.toString());

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            this.logger.error("Could not set the health value for the user.", exception);
            return false;
        }
    }

    public boolean changeHealthByUserId(UUID userId, int change) {
        if (!this.findByUserId(userId).isPresent()) {
            return false;
        }

        try (
            Connection connection = this.storage.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_HEALTH_CHANGE_BY_UUID)
        ) {
            statement.setInt(1, change);
            statement.setString(2, userId.toString());

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            this.logger.error("Could not change the health value for the user.", exception);
            return false;
        }
    }

    public boolean deleteByUserId(UUID userId) {
        if (!this.findByUserId(userId).isPresent()) {
            return false;
        }

        try (
            Connection connection = this.storage.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_USER_BY_UUID)
        ) {
            statement.setString(1, userId.toString());

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            this.logger.error("Could not delete the user from the database.", exception);
            return false;
        }
    }

}
