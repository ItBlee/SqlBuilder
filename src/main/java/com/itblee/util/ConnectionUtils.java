package com.itblee.util;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public final class ConnectionUtils {

    private static DataSource dbSource;

    private ConnectionUtils() {
        throw new AssertionError();
    }

    public static DataSource getDataSource() {
        if (dbSource == null) {
            ResourceBundle bundle = ResourceBundle.getBundle("db");
            MysqlDataSource source = new MysqlDataSource();
            source.setServerName(bundle.getString("db.server"));
            source.setPortNumber(Integer.parseInt(bundle.getString("db.port")));
            source.setDatabaseName(bundle.getString("db.name"));
            source.setUser(bundle.getString("db.username"));
            source.setPassword(bundle.getString("db.password"));
            dbSource = source;
        }
        return dbSource;
    }

    public static Connection createConnection() throws SQLException, ClassNotFoundException {
        return getDataSource().getConnection();
    }

    public static void setParameter(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object parameter = params[i];
            int index = i + 1;
            if (parameter instanceof Long) {
                statement.setLong(index, (Long) parameter);
            } else if (parameter instanceof String) {
                statement.setString(index, (String) parameter);
            } else if (parameter instanceof Integer) {
                statement.setInt(index, (Integer) parameter);
            } else if (parameter instanceof Date) {
                statement.setDate(index, (Date) parameter);
            }
        }
    }

    public static void close(AutoCloseable... obs) {
        try {
            for (AutoCloseable o : obs)
                if (o != null)
                    o.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
