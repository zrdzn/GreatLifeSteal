package io.github.zrdzn.minecraft.greatlifesteal.elimination.infra;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.Elimination;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationException;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationRepository;
import io.github.zrdzn.minecraft.greatlifesteal.elimination.EliminationReviveStatus;

class SqlEliminationRepository implements EliminationRepository {

    private final DataSource dataSource;

    private final String createEliminationQuery;
    private final String findEliminationByPlayerUuidQuery;
    private final String findEliminationByPlayerNameQuery;
    private final String updateReviveByPlayerNameQuery;
    private final String removeEliminationByPlayerUuidQuery;

    public SqlEliminationRepository(DataSource dataSource, String createEliminationQuery, String findEliminationByPlayerUuidQuery,
                                    String findEliminationByPlayerNameQuery, String updateReviveByPlayerNameQuery,
                                    String removeEliminationByPlayerUuidQuery) {
        this.dataSource = dataSource;

        this.createEliminationQuery = createEliminationQuery;
        this.findEliminationByPlayerUuidQuery = findEliminationByPlayerUuidQuery;
        this.findEliminationByPlayerNameQuery = findEliminationByPlayerNameQuery;
        this.updateReviveByPlayerNameQuery = updateReviveByPlayerNameQuery;
        this.removeEliminationByPlayerUuidQuery = removeEliminationByPlayerUuidQuery;
    }

    @Override
    public Elimination createElimination(UUID playerUuid, String playerName, String action, String lastWorld) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(this.createEliminationQuery)) {
            statement.setString(1, playerUuid.toString());
            statement.setString(2, playerName);
            statement.setString(3, action);
            statement.setString(4, lastWorld);

            statement.executeUpdate();

            return this.findEliminationByPlayerUuid(playerUuid).orElseThrow(() ->
                    new EliminationException("An error occurred while finding freshly created elimination."));
        } catch (SQLException exception) {
            throw new EliminationException("An error occurred while creating an elimination.", exception);
        }
    }

    @Override
    public Optional<Elimination> findEliminationByPlayerUuid(UUID playerUuid) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(this.findEliminationByPlayerUuidQuery)) {
            statement.setString(1, playerUuid.toString());
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Optional.empty();
            }

            Elimination elimination = new Elimination(
                    result.getInt("id"),
                    result.getTimestamp("created_at").toInstant(),
                    playerUuid,
                    result.getString("player_name"),
                    result.getString("action"),
                    EliminationReviveStatus.valueOf(result.getString("revive")),
                    result.getString("last_world")
            );

            return Optional.of(elimination);
        } catch (SQLException exception) {
            throw new EliminationException("An error occurred while finding an elimination by player uuid.", exception);
        }
    }

    @Override
    public Optional<Elimination> findEliminationByPlayerName(String playerName) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(this.findEliminationByPlayerNameQuery)) {
            statement.setString(1, playerName);
            ResultSet result = statement.executeQuery();
            if (!result.next()) {
                return Optional.empty();
            }

            Elimination elimination = new Elimination(
                    result.getInt("id"),
                    result.getTimestamp("created_at").toInstant(),
                    UUID.fromString(result.getString("player_uuid")),
                    playerName,
                    result.getString("action"),
                    EliminationReviveStatus.valueOf(result.getString("revive")),
                    result.getString("last_world")
            );

            return Optional.of(elimination);
        } catch (SQLException exception) {
            throw new EliminationException("An error occurred while finding an elimination by player uuid.", exception);
        }
    }

    @Override
    public boolean updateReviveByPlayerName(String playerName, EliminationReviveStatus status) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(this.updateReviveByPlayerNameQuery)) {
            statement.setString(1, status.toString());
            statement.setString(2, playerName);

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new EliminationException("An error occurred while updating an elimination revive status.", exception);
        }
    }

    @Override
    public void removeEliminationByPlayerUuid(UUID playerUuid) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(this.removeEliminationByPlayerUuidQuery)) {
            statement.setString(1, playerUuid.toString());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new EliminationException("An error occurred while removing an elimination by player uuid.", exception);
        }
    }

}
