package phase3;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class hopsital extends Application {

    private Stage primaryStage;
    private Scene userSelectionScene;
    private Scene loginOrSignUpScene;
    private Scene loginScene;
    private Scene userInfoScene;

    private String currentUserId; // Store the current user's ID

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Create buttons for user selection
        Button staffButton = new Button("Staff");
        Button patientButton = new Button("Patient");

        // Set action for patient button
        patientButton.setOnAction(e -> showLoginOrSignUpScene());
        staffButton.setOnAction(e -> showLoginOrSignUpScene());

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

        // Create labels and fields for name, password, DOB, and sex
        Label nameLabel = new Label("Name:");
        Label passwordLabel = new Label("Password:");
        Label dobLabel = new Label("Date of Birth:");
        Label sexLabel = new Label("Sex:");
        TextField nameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField dobField = new TextField(); // You can use DatePicker for a more sophisticated date input
        TextField sexField = new TextField();

        // Add labels and fields to dialog layout
        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.getChildren().addAll(nameLabel, nameField, passwordLabel, passwordField, dobLabel, dobField, sexLabel, sexField);
        signUpDialog.getDialogPane().setContent(dialogLayout);

        // Add buttons for OK and Cancel
        ButtonType signUpButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        signUpDialog.getDialogPane().getButtonTypes().addAll(signUpButtonType, ButtonType.CANCEL);

        // Set action for sign-up button
        signUpDialog.setResultConverter(dialogButton -> {
            if (dialogButton == signUpButtonType) {
                String name = nameField.getText();
                String password = passwordField.getText();
                String dob = dobField.getText();
                String sex = sexField.getText();
                String id = generateRandomID();
                currentUserId = id; // Store the current user's ID
                String accountInfo = name + "," + id + "," + password + "," + dob + "," + sex;
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
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(",");
                            if (parts.length == 5 && parts[0].equals(name) && parts[2].equals(password)) {
                                currentUserId = parts[1]; // Set currentUserId upon successful login
                                showUserInfoScene(currentUserId); // Call method to show user info scene
                                return true;
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Error reading file: " + e.getMessage());
                    }
                }
            }
        }
        return false;
    }

    private void showUserInfoScene(String userId) {
        // Read user info from file
        Map<String, String> userInfo = readUserInfo(userId);

        // Create labels to display user info
        Label nameLabel = new Label("Name: " + userInfo.get("Name"));
        Label dobLabel = new Label("Date of Birth: " + userInfo.get("DOB"));
        Label sexLabel = new Label("Sex: " + userInfo.get("Sex"));

        // Read insurance info from file
        String insuranceInfo = readInsuranceInfo(userId);
        Label insuranceLabel = new Label("Insurance Info: " + insuranceInfo);

        // Read pharmacy info from file
        String pharmacyInfo = readPharmacyInfo(userId);
        Label pharmacyLabel = new Label("Pharmacy Info: " + pharmacyInfo);

        // Create buttons for editing insurance and pharmacy info
        Button editInsuranceButton = new Button("Edit Insurance Info");
        Button editPharmacyButton = new Button("Edit Pharmacy Info");

        // Set actions for the buttons
        editInsuranceButton.setOnAction(e -> showEditInsuranceDialog(userId));
        editPharmacyButton.setOnAction(e -> showEditPharmacyDialog(userId));

        // Layout for user info
        VBox userInfoLayout = new VBox(10);
        userInfoLayout.setPadding(new Insets(20));
        userInfoLayout.setAlignment(Pos.CENTER);
        userInfoLayout.getChildren().addAll(nameLabel, dobLabel, sexLabel, insuranceLabel, pharmacyLabel, editInsuranceButton, editPharmacyButton);

        // Create scene for user info
        userInfoScene = new Scene(userInfoLayout, 400, 250);

        // Set primary stage title and scene
        primaryStage.setTitle("User Information");
        primaryStage.setScene(userInfoScene);
    }

    private void showEditInsuranceDialog(String userId) {
        // Create dialog for editing insurance info
        Dialog<String> editInsuranceDialog = new Dialog<>();
        editInsuranceDialog.setTitle("Edit Insurance Info");

        // Create labels and fields for insurance info
        Label companyLabel = new Label("Insurance Company:");
        Label idLabel = new Label("Insurance ID:");
        TextField companyField = new TextField();
        TextField idField = new TextField();

        // Add labels and fields to dialog layout
        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.getChildren().addAll(companyLabel, companyField, idLabel, idField);
        editInsuranceDialog.getDialogPane().setContent(dialogLayout);

        // Add buttons for OK and Cancel
        ButtonType editInsuranceButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        editInsuranceDialog.getDialogPane().getButtonTypes().addAll(editInsuranceButtonType, ButtonType.CANCEL);

        // Set action for save button
        editInsuranceDialog.setResultConverter(dialogButton -> {
            if (dialogButton == editInsuranceButtonType) {
                String company = companyField.getText();
                String id = idField.getText();
                String insuranceInfo = "Insurance Company: " + company + "\nInsurance ID: " + id;
                appendInsuranceInfo(insuranceInfo, userId);
                // Update the insurance label in the user info scene
                showUserInfoScene(userId);
                return "Saved";
            }
            return null;
        });

        // Show dialog
        editInsuranceDialog.showAndWait();
    }

    private void showEditPharmacyDialog(String userId) {
        // Create dialog for editing pharmacy info
        Dialog<String> editPharmacyDialog = new Dialog<>();
        editPharmacyDialog.setTitle("Edit Pharmacy Info");

        // Create labels and fields for pharmacy info
        Label nameLabel = new Label("Pharmacy Name:");
        Label addressLabel = new Label("Pharmacy Address:");
        TextField nameField = new TextField();
        TextField addressField = new TextField();

        // Add labels and fields to dialog layout
        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.getChildren().addAll(nameLabel, nameField, addressLabel, addressField);
        editPharmacyDialog.getDialogPane().setContent(dialogLayout);

        // Add buttons for OK and Cancel
        ButtonType editPharmacyButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        editPharmacyDialog.getDialogPane().getButtonTypes().addAll(editPharmacyButtonType, ButtonType.CANCEL);

        // Set action for save button
        editPharmacyDialog.setResultConverter(dialogButton -> {
            if (dialogButton == editPharmacyButtonType) {
                String name = nameField.getText();
                String address = addressField.getText();
                String pharmacyInfo = "Pharmacy Name: " + name + "\nPharmacy Address: " + address;
                appendPharmacyInfo(pharmacyInfo, userId);
                // Update the pharmacy label in the user info scene
                showUserInfoScene(userId);
                return "Saved";
            }
            return null;
        });

        // Show dialog
        editPharmacyDialog.showAndWait();
    }

    private void appendInsuranceInfo(String insuranceInfo, String userId) {
        String fileName = userId + ".txt";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append("\n").append(insuranceInfo);
            System.out.println("Insurance information appended successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendPharmacyInfo(String pharmacyInfo, String userId) {
        String fileName = userId + ".txt";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append("\n").append(pharmacyInfo);
            System.out.println("Pharmacy information appended successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> readUserInfo(String userId) {
        Map<String, String> userInfo = new HashMap<>();
        String fileName = userId + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    userInfo.put("Name", parts[0]);
                    userInfo.put("DOB", parts[3]);
                    userInfo.put("Sex", parts[4]);
                } else if (parts.length == 2 && parts[0].startsWith("Insurance Company:")) {
                    userInfo.put("Insurance Company", parts[0].substring(18));
                    userInfo.put("Insurance ID", parts[1].substring(13));
                } else if (parts.length == 2 && parts[0].startsWith("Pharmacy Address:")) {
                    userInfo.put("Pharmacy Address", parts[0].substring(18));
                    userInfo.put("Pharmacy Name", parts[1].substring(15));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfo;
    }


    private String readInsuranceInfo(String userId) {
        StringBuilder insuranceInfo = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(userId + ".txt"))) {
            String line;
            boolean insuranceSection = false;
            while ((line = reader.readLine()) != null) {
                if (insuranceSection) {
                    insuranceInfo.append(line).append("\n");
                }
                if (line.startsWith("Insurance")) {
                    insuranceSection = true;
                    insuranceInfo.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return insuranceInfo.toString();
    }

    private String readPharmacyInfo(String userId) {
        StringBuilder pharmacyInfo = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(userId + ".txt"))) {
            String line;
            boolean pharmacySection = false;
            while ((line = reader.readLine()) != null) {
                if (pharmacySection) {
                    pharmacyInfo.append(line).append("\n");
                }
                if (line.startsWith("Pharmacy")) {
                    pharmacySection = true;
                    pharmacyInfo.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pharmacyInfo.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

