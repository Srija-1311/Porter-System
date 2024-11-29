package mypackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentService {
    private Connection conn;

    public PaymentService(Connection conn) {
        this.conn = conn;
    }

    public boolean isValidBookingId(int bookingId) throws SQLException {
    String query = "SELECT COUNT(*) FROM Bookings WHERE bookingId = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, bookingId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}


    public boolean recordPayment(int bookingId, float amount) throws SQLException {
        if (!isValidBookingId(bookingId)) {
            throw new SQLException("Invalid Booking ID. Ensure the booking exists.");
        }
    
        String query = "INSERT INTO Payments (bookingId, amount) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            stmt.setFloat(2, amount);
            return stmt.executeUpdate() > 0;
        }
    }
    
}
