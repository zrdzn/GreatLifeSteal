package io.github.zrdzn.minecraft.greatlifesteal.storage.sqlite.repository;

import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRepository;
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

    private static final String INSERT = "INSERT INTO gls_eliminations (created_at, player_uuid, action) VALUES (?, ?, ?);";
    private static final String SELECT_BY_ID = "SELECT created_at, player_uuid, action FROM gls_eliminations WHERE id = ?;";
    private static final String SELECT_BY_UUID = "SELECT id, created_at, action FROM gls_eliminations WHERE player_uuid = ?;";
    private static final String SELECT_ALL = "SELECT id, created_at, player_uuid, action FROM gls_eliminations;";
    private static final String DELETE_BY_ID = "DELETE FROM gls_eliminations WHERE id = ?;";
    private static final String DELETE_BY_UUID = "DELETE FROM gls_eliminations WHERE player_uuid = ?;";

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
                statement.setString(3, elimination.getAction());

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
                    elimination.setAction(result.getString("action"));

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
                elimination.setAction(result.getString("action"));

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
                elimination.setAction(result.getString("action"));

                return Optional.of(elimination);
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

}
