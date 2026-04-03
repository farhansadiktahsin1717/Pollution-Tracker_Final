package com.example.pollutiontracker;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public final class DatabaseInitializer {

    private static final String DATABASE_MISSING_SQL_STATE = "3D000";
    private static final String DATABASE_NAME_PATTERN = "[A-Za-z0-9_]+";
    private static final String SCHEMA_RESOURCE = "/com/example/pollutiontracker/db/postgresql-schema.sql";

    private static volatile boolean initialized;

    private DatabaseInitializer() {
    }

    public static void initialize() throws SQLException {
        if (initialized) {
            return;
        }

        synchronized (DatabaseInitializer.class) {
            if (initialized) {
                return;
            }

            ensureDatabaseExists();
            applySchema();
            initialized = true;
        }
    }

    private static void ensureDatabaseExists() throws SQLException {
        try (Connection ignored = DBConnection.openApplicationConnection()) {
            // Database already exists and is reachable.
        } catch (SQLException e) {
            if (!DATABASE_MISSING_SQL_STATE.equals(e.getSQLState())) {
                throw e;
            }

            if (!DatabaseConfig.isLocalDatabase()) {
                throw new SQLException(
                        "Remote PostgreSQL database is not reachable or does not exist. "
                                + "For Supabase, use the project connection string directly and do not rely on automatic database creation.",
                        e
                );
            }

            createDatabase();
        }
    }

    private static void createDatabase() throws SQLException {
        String databaseName = DatabaseConfig.getDatabaseName();

        if (!databaseName.matches(DATABASE_NAME_PATTERN)) {
            throw new SQLException("Unsupported database name: " + databaseName);
        }

        try (Connection connection = DBConnection.openAdminConnection();
             PreparedStatement checkStatement = connection.prepareStatement(
                     "SELECT 1 FROM pg_database WHERE datname = ?"
             )) {

            checkStatement.setString(1, databaseName);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }
            }

            try (Statement createStatement = connection.createStatement()) {
                createStatement.execute("CREATE DATABASE \"" + databaseName + "\"");
            }
        }
    }

    private static void applySchema() throws SQLException {
        String script = loadSchemaScript();
        String executableScript = script.lines()
                .filter(line -> !line.stripLeading().startsWith("--"))
                .collect(Collectors.joining(System.lineSeparator()));
        String[] statements = executableScript.split(";\\s*(?:\\r?\\n|$)");

        try (Connection connection = DBConnection.openApplicationConnection()) {
            for (String statementText : statements) {
                String sql = statementText.trim();
                if (sql.isEmpty()) {
                    continue;
                }

                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                }
            }
        }
    }

    private static String loadSchemaScript() throws SQLException {
        try (InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (inputStream == null) {
                throw new SQLException("Schema resource not found: " + SCHEMA_RESOURCE);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SQLException("Failed to read schema resource.", e);
        }
    }
}
