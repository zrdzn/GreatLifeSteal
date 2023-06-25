package io.github.zrdzn.minecraft.greatlifesteal.elimination.infra;

import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveStatus;
import io.github.zrdzn.minecraft.greatlifesteal.storage.SqliteStorage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import panda.std.Blank;
import panda.std.Result;

public class SqliteEliminationRepository implements EliminationRepository {

    private static final String INSERT_ELIMINATION = "INSERT INTO gls_eliminations (created_at, player_uuid, player_name, action, last_world) VALUES (?, ?, ?, ?, ?);";
    private static final String SELECT_ELIMINATION_BY_UUID = "SELECT id, created_at, player_name, action, revive, last_world FROM gls_eliminations WHERE player_uuid = ?;";
    private static final String SELECT_ELIMINATION_BY_NAME = "SELECT id, created_at, player_uuid, action, revive, last_world FROM gls_eliminations WHERE player_name = ?;";
    private static final String UPDATE_REVIVE_BY_NAME = "UPDATE gls_eliminations SET revive = ? WHERE player_name = ?;";
    private static final String DELETE_ELIMINATION_BY_UUID = "DELETE FROM gls_eliminations WHERE player_uuid = ?;";

    private final SqliteStorage storage;

    public SqliteEliminationRepository(SqliteStorage storage) {
        this.storage = storage;
    }

    @Override
    public Result<Elimination, Exception> saveElimination(Elimination elimination) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(INSERT_ELIMINATION)) {
                statement.setTimestamp(1, Timestamp.from(elimination.getCreatedAt()));
                statement.setString(2, elimination.getPlayerUuid().toString());
                statement.setString(3, elimination.getPlayerName());
                statement.setString(4, elimination.getAction());
                statement.setString(5, elimination.getLastWorld());

                statement.executeUpdate();

                return elimination;
            }
        });
    }

    @Override
    public Result<Optional<Elimination>, Exception> findEliminationByPlayerUuid(UUID playerUuid) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_ELIMINATION_BY_UUID)) {
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
                elimination.setLastWorld(result.getString("last_world"));

                return Optional.of(elimination);
            }
        });
    }

    @Override
    public Result<Optional<Elimination>, Exception> findEliminationByPlayerName(String playerName) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(SELECT_ELIMINATION_BY_NAME)) {
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
                elimination.setLastWorld(result.getString("last_world"));

                return Optional.of(elimination);
            }
        });
    }

    @Override
    public Result<Boolean, Exception> updateReviveByPlayerName(String playerName, EliminationReviveStatus status) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(UPDATE_REVIVE_BY_NAME)) {
                statement.setString(1, status.toString());
                statement.setString(2, playerName);

                return statement.executeUpdate() > 0;
            }
        });
    }

    @Override
    public Result<Blank, Exception> deleteEliminationByPlayerUuid(UUID playerUuid) {
        return Result.supplyThrowing(() -> {
            try (Connection connection = this.storage.getConnection();
                    PreparedStatement statement = connection.prepareStatement(DELETE_ELIMINATION_BY_UUID)) {
                statement.setString(1, playerUuid.toString());
                statement.executeUpdate();
            }

            return Blank.BLANK;
        });
    }

}
