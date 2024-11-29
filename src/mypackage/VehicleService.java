package mypackage;

import java.sql.*;

public class VehicleService {
    private Connection conn;

    public VehicleService(Connection conn) {
        this.conn = conn;
    }

    // Method to add a vehicle
    public boolean addVehicle(int porterId, float capacity, String availabilityStatus) throws SQLException {
        String insertQuery = "INSERT INTO Vehicles (porterId, capacity, availabilityStatus) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, porterId);
            stmt.setFloat(2, capacity);
            stmt.setString(3, availabilityStatus);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
    }

    // Method to validate if a vehicle ID exists
    public boolean isValidVehicleId(int vehicleId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Vehicles WHERE vehicleId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Method to update vehicle availability
    public boolean updateVehicleAvailability(int vehicleId, String newAvailabilityStatus) throws SQLException {
        String updateQuery = "UPDATE Vehicles SET availabilityStatus = ? WHERE vehicleId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, newAvailabilityStatus);
            stmt.setInt(2, vehicleId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    // Method to retrieve vehicle details
    public String getVehicleDetails(int vehicleId) throws SQLException {
        String query = "SELECT * FROM Vehicles WHERE vehicleId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "Vehicle ID: " + rs.getInt("vehicleId") +
                            ", Porter ID: " + rs.getInt("porterId") +
                            ", Capacity: " + rs.getFloat("capacity") +
                            ", Availability: " + rs.getString("availabilityStatus");
                }
            }
        }
        return "No vehicle found with ID: " + vehicleId;
    }
}
