package io.github.zrdzn.minecraft.greatlifesteal.datasource;

import org.bukkit.configuration.ConfigurationSection;

import java.sql.ResultSet;
import java.util.Map;
import java.util.Optional;

/**
 * Interface that is representing a data source used
 * for connections to a database.
 */
public interface DataSource {

    /**
     * Parse a data source from configuration section.
     * Could be used for setting fields that are required
     * to create a connection to a data source.
     *
     * @param section a bukkit configuration section
     *
     */
    void parse(ConfigurationSection section);

    /**
     * Get a map of the tables related to this data source.
     * A key is the table name and a value is the query
     * for its creation.
     */
    Map<String, String> getTables();

    /**
     * Execute the specified query to the data source
     * and return a result set or an empty optional if
     * an error occurred.
     *
     * @param query a sql query with optional placeholders for replacements
     * @param replacements replacements that will replace additional placeholders in the query
     *
     * @return an optional result that comes from the executed query
     */
    Optional<ResultSet> query(String query, Object... replacements);

    /**
     * Execute the specified query to the data source
     * and return affected rows or -1 if an error occurred
     *
     * @param query a sql query with optional placeholders for replacements
     * @param replacements replacements that will replace additional placeholders in the query
     *
     * @return an amount of updated rows, -1 if an error occurred
     */
    int update(String query, Object... replacements);

}
