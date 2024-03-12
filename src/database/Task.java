package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Task {
    private PreparedStatement statement;

    public Task(String stmt, String stmtType, Object... parameters) throws SQLException {
        this.statement = Database.db.prepareStatement(stmt);
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }

        if (stmtType.equals("query")) {
            this.executeQuery();
        } else if (stmtType.equals("update")) {
            this.executeUpdate();
        } else{
            throw new IllegalArgumentException("Unknown statement type");
        }
    }

    private ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }

    private int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }
}