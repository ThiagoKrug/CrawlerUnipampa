package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    public synchronized Connection getConnection() {
        return this.getConnection("jdbc:mysql://localhost/casual", "root", "");
    }

    public Connection getTestConnection() {
        return this.getConnection("jdbc:mysql://localhost/casual_test", "root", "");
    }

    private Connection getConnection(String url, String user, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
