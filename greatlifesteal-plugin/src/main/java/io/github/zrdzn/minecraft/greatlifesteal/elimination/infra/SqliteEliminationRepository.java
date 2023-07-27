package io.github.zrdzn.minecraft.greatlifesteal.elimination.infra;

import javax.sql.DataSource;

public class SqliteEliminationRepository extends SqlEliminationRepository {

    private static final String CREATE_ELIMINATION = "INSERT INTO gls_eliminations (player_uuid, player_name, action, last_world) VALUES (?, ?, ?, ?);";
    private static final String FIND_ELIMINATION_BY_PLAYER_UUID = "SELECT id, created_at, player_name, action, revive, last_world FROM gls_eliminations WHERE player_uuid = ?;";
    private static final String FIND_ELIMINATION_BY_PLAYER_NAME = "SELECT id, created_at, player_uuid, action, revive, last_world FROM gls_eliminations WHERE player_name = ?;";
    private static final String UPDATE_REVIVE_BY_PLAYER_NAME = "UPDATE gls_eliminations SET revive = ? WHERE player_name = ?;";
    private static final String REMOVE_ELIMINATION_BY_PLAYER_UUID = "DELETE FROM gls_eliminations WHERE player_uuid = ?;";

    public SqliteEliminationRepository(DataSource dataSource) {
        super(dataSource, CREATE_ELIMINATION, FIND_ELIMINATION_BY_PLAYER_UUID, FIND_ELIMINATION_BY_PLAYER_NAME, UPDATE_REVIVE_BY_PLAYER_NAME, REMOVE_ELIMINATION_BY_PLAYER_UUID);
    }

}
