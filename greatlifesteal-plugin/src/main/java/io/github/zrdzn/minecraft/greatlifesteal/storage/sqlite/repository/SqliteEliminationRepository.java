package io.github.zrdzn.minecraft.greatlifesteal.storage.sqlite.repository;

import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveStatus;
import io.github.zrdzn.minecraft.greatlifesteal.storage.sqlite.SqliteStorage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import panda.std.Blank;
import panda.std.Result;

public class SqliteEliminationRepository implements EliminationRepository {

    private static final String INSERT = "INSERT INTO gls_eliminations (created_at, player_uuid, player_name, action) VALUES (?, ?, ?, ?);";
    private static final String SELECT_BY_ID = "SELECT created_at, player_uuid, player_name, action, revive FROM gls_eliminations WHERE id = ?;";
    private static final String SELECT_BY_UUID = "SELECT id, created_at, player_name, action, revive FROM gls_eliminations WHERE player_uuid = ?;";
    private static final String SELECT_BY_NAME = "SELECT id, created_at, player_uuid, action, revive FROM gls_eliminations WHERE player_name = ?;";
    private static final String SELECT_ALL = "SELECT id, created_at, player_uuid, player_name, action, revive FROM gls_eliminations;";
    private static final String UPDATE_REVIVE_BY_ID = "UPDATE gls_eliminations SET revive = ? WHERE id = ?;";
    private static final String UPDATE_REVIVE_BY_UUID = "UPDATE gls_eliminations SET revive = ? WHERE player_uuid = ?;";
    private static final String UPDATE_REVIVE_BY_NAME = "UPDATE gls_eliminations SET revive = ? WHERE player_name = ?;";
    private static final String DELETE_BY_ID = "DELETE FROM gls_eliminations WHERE id = ?;";
    private static final String DELETE_BY_UUID = "DELETE FROM gls_eliminations WHERE player_uuid = ?;";
    private static final String DELETE_BY_NAME = "DELETE FROM gls_eliminations WHERE player_name = ?;";

    private final SqliteStorage storage;

    public SqliteEliminationRepository(SqliteStorage storage) {
        this.storage = storage;
    }

    @Override
    public Result<Elimination, Exception> save(Elimination elimination) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(INSERT)) {
                statement.setTimestamp(1, Timestamp.from(elimination.getCreatedAt()));
                statement.setString(2, elimination.getPlayerUuid().toString());
                statement.setString(3, elimination.getPlayerName());
                statement.setString(4, elimination.getAction());

                statement.executeUpdate();

                return elimination;
            }
        });
    }

    @Override
    public Result<List<Elimination>, Exception> listAll() {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_ALL)) {
                List<Elimination> eliminations = new ArrayList<>();

                ResultSet result = statement.executeQuery();
                while (result.next()) {
                    Elimination elimination = new Elimination();
                    elimination.setId(result.getInt("id"));
                    elimination.setCreatedAt(result.getTimestamp("created_at").toInstant());
                    elimination.setPlayerUuid(UUID.fromString(result.getString("player_uuid")));
                    elimination.setPlayerName(result.getString("player_name"));
                    elimination.setAction(result.getString("action"));
                    elimination.setRevive(EliminationReviveStatus.valueOf(result.getString("revive")));

                    eliminations.add(elimination);
                }

                return eliminations;
            }
        });
    }

    @Override
    public Result<Optional<Elimination>, Exception> findById(int id) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
                statement.setInt(1, id);
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return Optional.empty();
                }

                Elimination elimination = new Elimination();
                elimination.setId(id);
                elimination.setCreatedAt(result.getTimestamp("created_at").toInstant());
                elimination.setPlayerUuid(UUID.fromString(result.getString("player_uuid")));
                elimination.setPlayerName(result.getString("player_name"));
                elimination.setAction(result.getString("action"));
                elimination.setRevive(EliminationReviveStatus.valueOf(result.getString("revive")));

                return Optional.of(elimination);
            }
        });
    }

    @Override
    public Result<Optional<Elimination>, Exception> findByPlayerUuid(UUID playerUuid) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_BY_UUID)) {
                statement.setString(1, playerUuid.toString());
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return Optional.empty();
                }

                Elimination elimination = new Elimination();
                elimination.setId(result.getInt("id"));
                elimination.setCreatedAt(result.getTimestamp("created_at").toInstant());
                elimination.setPlayerUuid(playerUuid);
                elimination.setPlayerName(result.getString("player_name"));
                elimination.setAction(result.getString("action"));
                elimination.setRevive(EliminationReviveStatus.valueOf(result.getString("revive")));

                return Optional.of(elimination);
            }
        });
    }

    @Override
    public Result<Optional<Elimination>, Exception> findByPlayerName(String playerName) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME)) {
                statement.setString(1, playerName);
                ResultSet result = statement.executeQuery();
                if (!result.next()) {
                    return Optional.empty();
                }

                Elimination elimination = new Elimination();
                elimination.setId(result.getInt("id"));
                elimination.setCreatedAt(result.getTimestamp("created_at").toInstant());
                elimination.setPlayerUuid(UUID.fromString(result.getString("player_uuid")));
                elimination.setPlayerName(playerName);
                elimination.setAction(result.getString("action"));
                elimination.setRevive(EliminationReviveStatus.valueOf(result.getString("revive")));

                return Optional.of(elimination);
            }
        });
    }

    @Override
    public Result<Boolean, Exception> updateReviveById(int id, EliminationReviveStatus status) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(UPDATE_REVIVE_BY_ID)) {
                statement.setString(1, status.toString());
                statement.setInt(2, id);

                return statement.executeUpdate() > 0;
            }
        });
    }

    @Override
    public Result<Boolean, Exception> updateReviveByPlayerUuid(UUID playerUuid, EliminationReviveStatus status) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(UPDATE_REVIVE_BY_UUID)) {
                statement.setString(1, status.toString());
                statement.setString(2, playerUuid.toString());

                return statement.executeUpdate() > 0;
            }
        });
    }

    @Override
    public Result<Boolean, Exception> updateReviveByPlayerName(String playerName, EliminationReviveStatus status) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(UPDATE_REVIVE_BY_NAME)) {
                statement.setString(1, status.toString());
                statement.setString(2, playerName);

                return statement.executeUpdate() > 0;
            }
        });
    }

    @Override
    public Result<Blank, Exception> deleteById(int id) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }

            return Blank.BLANK;
        });
    }

    @Override
    public Result<Blank, Exception> deleteByPlayerUuid(UUID playerUuid) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(DELETE_BY_UUID)) {
                statement.setString(1, playerUuid.toString());
                statement.executeUpdate();
            }

            return Blank.BLANK;
        });
    }

    @Override
    public Result<Blank, Exception> deleteByPlayerName(String playerName) {
        return Result.attempt(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(DELETE_BY_NAME)) {
                statement.setString(1, playerName);
                statement.executeUpdate();
            }

            return Blank.BLANK;
        });
    }

}
