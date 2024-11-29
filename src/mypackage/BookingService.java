package mypackage;

import java.sql.*;

public class BookingService {
    private Connection conn;

    public BookingService(Connection conn) {
        this.conn = conn;
    }

    public String createBooking(int customerId, String pickupAddress, String dropAddress, float weight) throws SQLException {
        String availableVehicleQuery = "SELECT vehicleId FROM Vehicles WHERE availabilityStatus = 'Available' AND capacity >= ? FETCH FIRST 1 ROWS ONLY";
        String assignVehicleAndCreateBooking = "INSERT INTO Bookings (customerId, vehicleId, pickupAddress, dropAddress, weight, status) VALUES (?, ?, ?, ?, ?, 'Booked')";
        String updateVehicleStatus = "UPDATE Vehicles SET availabilityStatus = 'Unavailable' WHERE vehicleId = ?";
        float ratePerKg = 10.0f; // Example rate per kilogram for calculation
        float payableAmount = weight * ratePerKg;

        try (PreparedStatement stmtVehicle = conn.prepareStatement(availableVehicleQuery);
             PreparedStatement stmtBooking = conn.prepareStatement(assignVehicleAndCreateBooking, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtUpdate = conn.prepareStatement(updateVehicleStatus)) {

            // Step 1: Find an available vehicle
            stmtVehicle.setFloat(1, weight);
            ResultSet rs = stmtVehicle.executeQuery();
            if (!rs.next()) {
                return "No available vehicles meet the required capacity.";
            }
            int assignedVehicleId = rs.getInt("vehicleId");

            // Step 2: Create the booking
            stmtBooking.setInt(1, customerId);
            stmtBooking.setInt(2, assignedVehicleId);
            stmtBooking.setString(3, pickupAddress);
            stmtBooking.setString(4, dropAddress);
            stmtBooking.setFloat(5, weight);

            int rowsAffected = stmtBooking.executeUpdate();
            if (rowsAffected == 0) {
                return "Booking creation failed.";
            }

            // Step 3: Update vehicle status to 'Unavailable'
            stmtUpdate.setInt(1, assignedVehicleId);
            stmtUpdate.executeUpdate();

            // Step 4: Return the details to the user
            return String.format("Booking successful! Vehicle ID: %d, Payable Amount: %.2f", assignedVehicleId, payableAmount);
        }
    }
}
