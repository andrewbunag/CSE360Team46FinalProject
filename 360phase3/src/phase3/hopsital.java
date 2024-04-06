package phase3;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class hopsital extends Application {

    private Stage primaryStage;
    private Scene userSelectionScene;
    private Scene loginOrSignUpScene;
    private Scene loginScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Create buttons for user selection
        Button staffButton = new Button("Staff");
        Button patientButton = new Button("Patient");

        // Set action for patient button
        patientButton.setOnAction(e -> showLoginOrSignUpScene());

        // Layout for user selection
        HBox userSelectionLayout = new HBox(10);
        userSelectionLayout.setPadding(new Insets(20));
        userSelectionLayout.setAlignment(Pos.CENTER);
        userSelectionLayout.getChildren().addAll(staffButton, patientButton);

        // Layout for login or sign-up
        VBox loginOrSignUpLayout = new VBox(10);
        loginOrSignUpLayout.setPadding(new Insets(20));
        loginOrSignUpLayout.setAlignment(Pos.CENTER);

        // Create buttons for login or sign-up
        Button loginButton = new Button("Login");
        Button signUpButton = new Button("Sign Up");

        // Set action for login button
        loginButton.setOnAction(e -> showLoginScene());

        // Set action for sign-up button
        signUpButton.setOnAction(e -> showSignUpDialog());

        // Add buttons to login or sign-up layout
        loginOrSignUpLayout.getChildren().addAll(loginButton, signUpButton);

        // Create scenes
        userSelectionScene = new Scene(userSelectionLayout, 400, 150);
        loginOrSignUpScene = new Scene(loginOrSignUpLayout, 400, 150);

        // Set primary stage title and scene
        primaryStage.setTitle("Hospital User Selection");
        primaryStage.setScene(userSelectionScene);
        primaryStage.show();
    }

    private void showLoginOrSignUpScene() {
        primaryStage.setScene(loginOrSignUpScene);
    }

    private void showSignUpDialog() {
        // Create dialog for sign-up
        Dialog<String> signUpDialog = new Dialog<>();
        signUpDialog.setTitle("Create Account");

        // Create labels and fields for name and password
        Label nameLabel = new Label("Name:");
        Label passwordLabel = new Label("Password:");
        TextField nameField = new TextField();
        PasswordField passwordField = new PasswordField();

        // Add labels and fields to dialog layout
        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.getChildren().addAll(nameLabel, nameField, passwordLabel, passwordField);
        signUpDialog.getDialogPane().setContent(dialogLayout);

        // Add buttons for OK and Cancel
        ButtonType signUpButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        signUpDialog.getDialogPane().getButtonTypes().addAll(signUpButtonType, ButtonType.CANCEL);

        // Set action for sign-up button
        signUpDialog.setResultConverter(dialogButton -> {
            if (dialogButton == signUpButtonType) {
                String name = nameField.getText();
                String password = passwordField.getText();
                String id = generateRandomID();
                String accountInfo = name + "," + id + "," + password;
                saveAccountInfo(accountInfo, id); // Save account info to file
                return "Signed Up";
            }
            return null;
        });

        // Show dialog
        signUpDialog.showAndWait();
    }

    private String generateRandomID() {
        Random random = new Random();
        int id = random.nextInt(90000) + 10000; // Generate random 5-digit number
        return String.valueOf(id);
    }

    private void saveAccountInfo(String accountInfo, String id) {
        String fileName = id + ".txt";
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(accountInfo);
            writer.close();
            System.out.println("Account information saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showLoginScene() {
        // Create login layout
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setAlignment(Pos.CENTER);

        // Create labels and fields for login
        Label nameLabel = new Label("Name:");
        Label passwordLabel = new Label("Password:");
        TextField nameField = new TextField();
        PasswordField passwordField = new PasswordField();

        // Add labels and fields to login layout
        loginLayout.getChildren().addAll(nameLabel, nameField, passwordLabel, passwordField);

        // Create login button
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String name = nameField.getText();
            String password = passwordField.getText();
            if (verifyLogin(name, password)) {
                System.out.println("Login successful");
                // Code to navigate to another view upon successful login
            } else {
                System.out.println("Invalid credentials");
                // Code to display error message
            }
        });

        // Add login button to layout
        loginLayout.getChildren().add(loginButton);

        // Set scene with login layout
        loginScene = new Scene(loginLayout, 400, 150);
        primaryStage.setScene(loginScene);
    }

    private boolean verifyLogin(String name, String password) {
        // Verify login credentials by reading from file
        String fileName = name + ".txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[2].equals(password)) {
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
