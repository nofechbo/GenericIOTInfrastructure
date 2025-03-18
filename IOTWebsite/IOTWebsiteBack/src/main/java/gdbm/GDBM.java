package gdbm;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GDBM {
    private Connection connection;

    public GDBM(String DBName, String url, String username, String password) throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        sendSQLUpdate("CREATE DATABASE IF NOT EXISTS " + DBName + ";");
        sendSQLUpdate("USE "  + DBName + ";");
        //connection = DriverManager.getConnection(url + "/" + DBName, username, password);
    }

    public void createTable(String name, List<Map.Entry<DataTypes, String>> values, List<String> varcharsLengths, String primaryKey) throws SQLException {
        sendSQLUpdate(getQueryCreateTable(name, values, varcharsLengths, primaryKey));
    }

    public void createTable(String name, List<Map.Entry<DataTypes, String>> values, List<String> varcharsLengths) throws SQLException {
        sendSQLUpdate(getQueryCreateTable(name, values, varcharsLengths, null));
    }

    public void createTable(String name, List<Map.Entry<DataTypes, String>> values, List<String> varcharsLengths, String primaryKey, String foreignKey, String referencedTable, String referencedAttribute) throws SQLException {
        StringBuilder query = new StringBuilder(getQueryCreateTable(name, values, varcharsLengths, primaryKey));
        sendSQLUpdate(query.substring(0, query.length()-1) + ", " + setForeignKey(foreignKey, referencedTable, referencedAttribute));
    }

    public void createTable(String name, List<Map.Entry<DataTypes, String>> values, List<String> varcharsLengths, String foreignKey, String referencedTable, String referencedAttribute) throws SQLException {
        StringBuilder query = new StringBuilder(getQueryCreateTable(name, values, varcharsLengths, null));
        sendSQLUpdate(query.substring(0, query.length()-1) + ", " + setForeignKey(foreignKey, referencedTable, referencedAttribute));
    }

    private String getQueryCreateTable(String name, List<Map.Entry<DataTypes, String>> values, List<String> varcharsLengths, String primaryKey) {
        if (primaryKey != null) {
            return "CREATE TABLE IF NOT EXISTS " + name + " (" +
                    getTableArgs(values, varcharsLengths) +
                    ", PRIMARY KEY (" + primaryKey + "))";
        }
        else {
            return "CREATE TABLE IF NOT EXISTS " + name + " (" +
                    getTableArgs(values, varcharsLengths) + ")";
        }
    }

    private String getTableArgs(List<Map.Entry<DataTypes, String>> values, List<String> varcharsLengths) {
        StringBuilder args = new StringBuilder();
        int i = 0;

        for (Map.Entry<DataTypes, String> value : values) {
            args.append(value.getValue()).append(" ");

            if (value.getKey().name().equals("VARCHAR")) {
                String type = "VARCHAR(" + varcharsLengths.get(i) + "), ";
                ++i;
                args.append(type);
            } else {
                args.append(value.getKey().name()).append(", ");
            }
        }

        return args.substring(0, args.length()-2);
    }

    private String setForeignKey(String foreignKey, String referencedTable, String referencedAttribute) {
        return "FOREIGN KEY (" + foreignKey + ") REFERENCES " + referencedTable + "(" + referencedAttribute + "))";
    }

    public void insertRecord(String table, List<String> data) throws SQLException {
        StringBuilder args = new StringBuilder();
        for (String val : data) {
            args.append(val).append(", ");
        }
        String query = "INSERT INTO " + table +
                " VALUES (" + args.substring(0, args.length()-2) + ")";

        sendSQLUpdate(query);
    }

    public List<List<String>> getRecords(String table) throws SQLException {
        String query = "SELECT * FROM " + table;

        return parseResultSet(sendSQLQuery(query));
    }

    public List<List<String>> getRecords(String table, String attribute) throws SQLException {
        String query = "SELECT " + attribute + " FROM " + table;

        return parseResultSet(sendSQLQuery(query));
    }
    public List<List<String>> getRecords(String table, String attribute, String val) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + attribute + " = " + val;

        return parseResultSet(sendSQLQuery(query));
    }

    public void deleteTable(String table) throws SQLException {
        String query = "DROP TABLE IF EXISTS " + table;

        sendSQLUpdate(query);
    }
    public void deleteRecords(String table) throws SQLException {
        String query = "TRUNCATE " + table;

        sendSQLUpdate(query);
    }
    public void deleteRecords(String table, String attribute, String val) throws SQLException {
        String query = "DELETE FROM " + table +
                " WHERE " + attribute + " = '" + val + "'";

        sendSQLUpdate(query);
    }

    public void updateRecord(String table, String attribute, String val, String newVal) throws SQLException {
        String query = "UPDATE " + table +
                " SET " + attribute + " = '" + newVal +
                "' WHERE " + attribute + " = '" + val + "'";

        sendSQLUpdate(query);
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private void sendSQLUpdate(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(query);
    }

    private ResultSet sendSQLQuery(String query) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    private List<List<String>> parseResultSet(ResultSet res) throws SQLException {
        List<List<String>> records = new ArrayList<>();

        while(res.next()) {
            int columnCount = res.getMetaData().getColumnCount();
            List<String> row = new ArrayList<>();
            for(int i = 1; i <= columnCount; ++i) {
                row.add(res.getString(i));
            }
            records.add(row);
        }
        return records;
    }

    public void beginTransaction() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not available.");
        }
        connection.setAutoCommit(false); // Start transaction
    }

    public void commitTransaction() throws SQLException {
        if (connection != null) {
            connection.commit(); // Commit transaction
            connection.setAutoCommit(true); // Reset auto-commit
        }
    }

    public void rollbackTransaction() throws SQLException {
        if (connection != null) {
            connection.rollback(); // Rollback transaction
            connection.setAutoCommit(true); // Reset auto-commit
        }
    }

    public List<List<String>> selectRecords(String table, List<String> columns, String whereClause) throws SQLException {
        String columnString = String.join(", ", columns);
        String query = "SELECT " + columnString + " FROM " + table;

        if (whereClause != null && !whereClause.trim().isEmpty()) {
            query += " WHERE " + whereClause;
        }

        return parseResultSet(sendSQLQuery(query));
    }

}

