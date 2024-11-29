import java.sql.Connection;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mypackage.BookingService;
import mypackage.DBConnection;
import mypackage.PaymentService;
import mypackage.UserService;
import mypackage.VehicleService;

public class MainApp extends Application {
    private Connection conn;

    // Services
    private UserService userService;
    private VehicleService vehicleService;
    private BookingService bookingService;
    private PaymentService paymentService;

    @SuppressWarnings("unused")
    @Override
    public void start(Stage primaryStage) throws SQLException {
        // Establish DB connection
        conn = DBConnection.getConnection();

        // Instantiate the services with the DB connection
        userService = new UserService(conn);
        vehicleService = new VehicleService(conn);
        bookingService = new BookingService(conn);
        paymentService = new PaymentService(conn);

        // UI elements
        VBox vbox = new VBox(10);
        Label label = new Label("Welcome to Porter App");

        // Buttons
        Button registerButton = new Button("Register User");
        Button addVehicleButton = new Button("Add Vehicle");
        Button createBookingButton = new Button("Create Booking");
        Button recordPaymentButton = new Button("Record Payment");

        // Button actions
        registerButton.setOnAction(e -> registerUser());
        addVehicleButton.setOnAction(e -> addVehicle());
        createBookingButton.setOnAction(e -> createBooking());
        recordPaymentButton.setOnAction(e -> recordPayment());

        // Add buttons to layout
        vbox.getChildren().addAll(label, registerButton, addVehicleButton, createBookingButton, recordPaymentButton);

        // Create scene and set stage
        Scene scene = new Scene(vbox, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Porter App");
        primaryStage.show();
    }

    // User registration
    private void registerUser() {
        // Sample user registration dialog
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Register User");
        usernameDialog.setHeaderText("Enter username:");
        String username = usernameDialog.showAndWait().orElse("");
        if (username.isEmpty()) return;

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Register User");
        passwordDialog.setHeaderText("Enter password:");
        String password = passwordDialog.showAndWait().orElse("");
        if (password.isEmpty()) return;

        ChoiceDialog<String> roleDialog = new ChoiceDialog<>("customer", "customer", "porter");

        roleDialog.setTitle("Register User");
        roleDialog.setHeaderText("Select role:");
        String role = roleDialog.showAndWait().orElse("Customer");

        try {
            boolean success = userService.registerUser(username, password, role);
            if (success) {
                showAlert("Success", "User registered successfully!");
            } else {
                showAlert("Error", "User registration failed.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the stack trace
            showAlert("Error", "Database error: " + e.getMessage()); // Display error message
        }
        
    }

    // Add vehicle for a porter
    private void addVehicle() {
        // Sample vehicle addition dialog
        try{
        TextInputDialog capacityDialog = new TextInputDialog();
        capacityDialog.setTitle("Add Vehicle");
        capacityDialog.setHeaderText("Enter vehicle capacity:");
        String capacityStr = capacityDialog.showAndWait().orElse("");
        if (capacityStr.isEmpty()) return;
        
        float capacity = Float.parseFloat(capacityStr);
        
        TextInputDialog porterIdDialog = new TextInputDialog();
        porterIdDialog.setTitle("Add Vehicle");
        porterIdDialog.setHeaderText("Enter porter user ID:");
        String porterIdStr = porterIdDialog.showAndWait().orElse("");
        if (porterIdStr.isEmpty()) return;
        
        int porterId = Integer.parseInt(porterIdStr);

        ChoiceDialog<String> availabilityDialog = new ChoiceDialog<>("Available", "Available", "Unavailable");
        availabilityDialog.setTitle("Add Vehicle");
        availabilityDialog.setHeaderText("Select vehicle availability:");
        String availability = availabilityDialog.showAndWait().orElse("Available");

        
            boolean success = vehicleService.addVehicle(porterId, capacity, availability);
            if (success) {
                showAlert("Success", "Vehicle added successfully!");
            } else {
                showAlert("Error", "Vehicle addition failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        }
    }

    // Create booking for customer
    private void createBooking() {
        // Booking dialog
        TextInputDialog customerIdDialog = new TextInputDialog();
        customerIdDialog.setTitle("Create Booking");
        customerIdDialog.setHeaderText("Enter customer user ID:");
        String customerIdStr = customerIdDialog.showAndWait().orElse("");
        if (customerIdStr.isEmpty()) return;
    
        int customerId = Integer.parseInt(customerIdStr);
    
        TextInputDialog pickupDialog = new TextInputDialog();
        pickupDialog.setTitle("Create Booking");
        pickupDialog.setHeaderText("Enter pickup address:");
        String pickupAddress = pickupDialog.showAndWait().orElse("");
        if (pickupAddress.isEmpty()) return;
    
        TextInputDialog dropDialog = new TextInputDialog();
        dropDialog.setTitle("Create Booking");
        dropDialog.setHeaderText("Enter drop address:");
        String dropAddress = dropDialog.showAndWait().orElse("");
        if (dropAddress.isEmpty()) return;
    
        TextInputDialog weightDialog = new TextInputDialog();
        weightDialog.setTitle("Create Booking");
        weightDialog.setHeaderText("Enter weight of goods:");
        String weightStr = weightDialog.showAndWait().orElse("");
        if (weightStr.isEmpty()) return;
    
        float weight = Float.parseFloat(weightStr);
    
        try {
            String result = bookingService.createBooking(customerId, pickupAddress, dropAddress, weight);
            showAlert("Booking Result", result);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error during booking creation.");
        }
    }
    

    // Record payment for a booking
    private void recordPayment() {
        // Sample payment recording dialog
        TextInputDialog bookingIdDialog = new TextInputDialog();
        bookingIdDialog.setTitle("Record Payment");
        bookingIdDialog.setHeaderText("Enter booking ID:");
        String bookingIdStr = bookingIdDialog.showAndWait().orElse("");
        if (bookingIdStr.isEmpty()) return;

        int bookingId = Integer.parseInt(bookingIdStr);

        TextInputDialog amountDialog = new TextInputDialog();
        amountDialog.setTitle("Record Payment");
        amountDialog.setHeaderText("Enter payment amount:");
        String amountStr = amountDialog.showAndWait().orElse("");
        if (amountStr.isEmpty()) return;

        float amount = Float.parseFloat(amountStr);

        boolean success=true;
        try {
            success = paymentService.recordPayment(bookingId, amount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (success) {
            showAlert("Success", "Payment recorded successfully!");
        } else {
            showAlert("Error", "Payment recording failed.");
        }
    }

    // Display alert dialogs
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args);
    }
}

