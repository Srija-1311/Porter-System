package mypackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private Connection conn;

    public UserService(Connection conn) {
        this.conn = conn;
    }

    public boolean registerUser(String username, String password, String role) throws SQLException {
        String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean isValidPorterId(int porterId) throws SQLException {
    String query = "SELECT COUNT(*) FROM Users WHERE userId = ? AND role = 'porter'";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, porterId);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }
}

    public boolean isValidCustomerId(int customerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE userId = ? AND role = 'customer'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }


}
