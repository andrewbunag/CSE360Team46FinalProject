package phase3;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class hopsital extends Application {

    private Stage primaryStage;
    private Scene userSelectionScene;
    private Scene loginOrSignUpScene;
    private Scene loginScene;
    private Scene userInfoScene;
    private Scene patientListScene;

    private String currentUserId; // Store the current user's ID
    private boolean isStaffClicked = false; // Track if the staff button was clicked


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Create buttons for user selection
        Button staffButton = new Button("Staff");
        Button patientButton = new Button("Patient");
        

        // Set action for patient button
        patientButton.setOnAction(e -> {
            isStaffClicked = false; // Reset the isStaffClicked flag
            showLoginOrSignUpScene();
        });
        staffButton.setOnAction(e -> {
            isStaffClicked = true; // Set the isStaffClicked flag
            showLoginOrSignUpScene();
        });

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
        userSelectionScene = new Scene(userSelectionLayout, 500, 500);
        loginOrSignUpScene = new Scene(loginOrSignUpLayout, 500, 500);

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
                String accountInfo = name + "," + id + "," + password + "," + dob + "," + sex + "," + (isStaffClicked ? "Staff" : "Patient");
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
            // Append "_staff" or "_patient" to the file name based on the user type
            if (isStaffClicked) {
                fileName = id + "_staff.txt";
            } else {
                fileName = id + "_patient.txt";
            }
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
        loginScene = new Scene(loginLayout, 500, 500);
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
                            if (parts.length == 6 && parts[0].equals(name) && parts[2].equals(password)) {
                                currentUserId = parts[1]; // Set currentUserId upon successful login
                                if (isStaffClicked && parts[5].equals("Staff")) {
                                    // If the staff button was clicked and the user is staff, show the patient list scene
                                    showPatientListScene();
                                } else if (!isStaffClicked && parts[5].equals("Patient")) {
                                    // If the staff button was not clicked and the user is patient, show the user info scene
                                    showUserInfoScene(currentUserId);
                                }
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

        // Create button for messaging
        Button messageButton = new Button("Message Clinic");
        messageButton.setOnAction(e -> Messages(userId, "Patient"));

        // Layout for user info
        VBox userInfoLayout = new VBox(10);
        userInfoLayout.setPadding(new Insets(20));
        userInfoLayout.setAlignment(Pos.CENTER);
        userInfoLayout.getChildren().addAll(nameLabel, dobLabel, sexLabel, insuranceLabel, pharmacyLabel, editInsuranceButton, editPharmacyButton, messageButton);

        // Create scene for user info
        userInfoScene = new Scene(userInfoLayout, 500, 500);

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
        String fileName = userId + "_patient.txt";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append("\n").append(insuranceInfo);
            System.out.println("Insurance information appended successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendPharmacyInfo(String pharmacyInfo, String userId) {
        String fileName = userId + "_patient.txt";
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append("\n").append(pharmacyInfo);
            System.out.println("Pharmacy information appended successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> readUserInfo(String userId) {
        Map<String, String> userInfo = new HashMap<>();
        String fileName = userId + "_patient.txt"; // Update the file name to match patient files
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) { // Ensure correct number of parts
                    userInfo.put("Name", parts[0]);
                    // Skip parts[1] as it contains the ID, which we already have
                    // Skip parts[2] as it contains the password, which we don't need here
                    userInfo.put("DOB", parts[3]);
                    userInfo.put("Sex", parts[4]);
                    // parts[5] contains the user type (e.g., "Patient")
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfo;
    }



    private String readInsuranceInfo(String userId) {
        StringBuilder insuranceInfo = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(userId + "_patient.txt"))) {
            String line;
            boolean insuranceSection = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Insurance")) {
                    insuranceSection = true;
                }
                if (insuranceSection) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(userId + "_patient.txt"))) {
            String line;
            boolean pharmacySection = false;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Pharmacy")) {
                    pharmacySection = true;
                }
                if (pharmacySection) {
                    pharmacyInfo.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pharmacyInfo.toString();
    }



    private void showPatientListScene() {
        // Read all patient IDs from the files
        List<String> patientIds = getAllPatientIds();

        // Create a ListView to display the patient IDs
        ListView<String> patientListView = new ListView<>();
        patientListView.setCellFactory(param -> new ListCell<String>() {
            private final Button NewVisitButton = new Button("New Visit");
            private final Button MessageButton = new Button("Message");
            private final HBox hbox = new HBox(NewVisitButton, MessageButton);

            {
                NewVisitButton.setOnAction(event -> {
                    String patientId = getItem();
                    NewVisitView(patientId);
                    // Handle edit action for the specific patient ID
                    // For example: showEditPatientDialog(patientId);
                });

                MessageButton.setOnAction(event -> {
                    String patientId = getItem();
                    Messages(patientId, "Staff");
                    // Handle delete action for the specific patient ID
                    // For example: deletePatient(patientId);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(hbox);
                }
            }
        });

        patientListView.getItems().addAll(patientIds);

        // Create a button to go back to the login scene
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showLoginScene());

        // Layout for the patient list scene
        VBox patientListLayout = new VBox(10);
        patientListLayout.setPadding(new Insets(20));
        patientListLayout.setAlignment(Pos.CENTER);
        patientListLayout.getChildren().addAll(new Label("List of Patients:"), patientListView, backButton);

        // Create scene for the patient list
        patientListScene = new Scene(patientListLayout, 500, 500);

        // Set primary stage title and scene
        primaryStage.setTitle("Patient List");
        primaryStage.setScene(patientListScene);
    }
    
    private void Messages(String patientId, String Sender) {
        // Create or retrieve the message history file for the patient
        File messageHistoryFile = getMessageHistoryFile(patientId);

        // Read the message history from the file
        List<String> messageHistory = readMessageHistory(messageHistoryFile);

        // Create UI components for displaying messages
        VBox messageHistoryLayout = createMessageHistoryLayout(messageHistory);
        TextField messageInputField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            // Handle sending of messages
            String message = messageInputField.getText();
            if (!message.isEmpty()) {
                sendMessage(messageHistoryFile, message, Sender);
                messageHistory.add(Sender+": " + message); // Add the sent message to the history
                messageHistoryLayout.getChildren().add(new Label(Sender+": " + message)); // Update UI
                messageInputField.clear(); // Clear the input field after sending
            }
        });

        
        Button backButton = new Button("Back");
        
        if(Sender =="Staff") {
        backButton.setOnAction(e -> {
            
            showPatientListScene();
        });
        }
        
        else {
        	backButton.setOnAction(e -> {
                
        		showUserInfoScene(patientId);
            });
        }
        
        
        // Layout for the messages scene
        VBox messagesLayout = new VBox(10);
        messagesLayout.setAlignment(Pos.TOP_CENTER);
        messagesLayout.getChildren().addAll(messageHistoryLayout, messageInputField, sendButton);
        Scene messagesScene = new Scene(messagesLayout, 500, 500);

        // Show the messages scene
        Stage messagesStage = new Stage();
        messagesStage.setScene(messagesScene);
        messagesStage.setTitle("Messages with Patient " + patientId);
        messagesStage.show();
    }

    private File getMessageHistoryFile(String patientId) {
        // The message history file name can be based on the patient ID
        return new File(patientId + "_messages.txt");
    }

    private List<String> readMessageHistory(File messageHistoryFile) {
        List<String> messageHistory = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(messageHistoryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messageHistory.add(line);
            }
        } catch (IOException e) {
            // Handle file not found or other IO errors
            e.printStackTrace();
        }
        return messageHistory;
    }

    private void sendMessage(File messageHistoryFile, String message, String sender) {
        try (FileWriter writer = new FileWriter(messageHistoryFile, true)) {
            writer.write(sender + ": " + message + "\n"); // Append the new message to the file
        } catch (IOException e) {
            // Handle IO errors
            e.printStackTrace();
        }
    }


    private VBox createMessageHistoryLayout(List<String> messageHistory) {
        VBox messageHistoryLayout = new VBox(5);
        messageHistoryLayout.setAlignment(Pos.TOP_LEFT);
        messageHistoryLayout.setPadding(new Insets(10));
        for (String message : messageHistory) {
            messageHistoryLayout.getChildren().add(new Label(message));
        }
        return messageHistoryLayout;
    }

    
    private void NewVisitView(String patientId) {
        Stage newVisitStage = new Stage();
        GridPane newVisitLayout = new GridPane();
        newVisitLayout.setAlignment(Pos.CENTER);
        newVisitLayout.setHgap(10);
        newVisitLayout.setVgap(10);
        newVisitLayout.setPadding(new Insets(20));

        // Create labels for each field
        Label dateLabel = new Label("Date:");
        Label heightLabel = new Label("Height:");
        Label weightLabel = new Label("Weight:");
        Label temperatureLabel = new Label("Temperature:");
        Label healthConcernsLabel = new Label("Health Concerns:");
        Label healthHistoryLabel = new Label("Health History:");
        Label doctorsNotesLabel = new Label("Doctor's Notes:");
        Label medicationLabel = new Label("Medication:");

        // Create text fields for each field
        TextField dateField = new TextField();
        TextField heightField = new TextField();
        TextField weightField = new TextField();
        TextField temperatureField = new TextField();
        TextField healthConcernsField = new TextField();
        TextField healthHistoryField = new TextField();
        TextField doctorsNotesField = new TextField();
        TextField medicationField = new TextField();

        // Add labels and text fields to the grid layout
        newVisitLayout.addColumn(0, dateLabel, heightLabel, weightLabel, temperatureLabel, healthConcernsLabel, healthHistoryLabel, doctorsNotesLabel, medicationLabel);
        newVisitLayout.addColumn(1, dateField, heightField, weightField, temperatureField, healthConcernsField, healthHistoryField, doctorsNotesField, medicationField);

        // Create save button
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            // Get values from text fields
            String date = dateField.getText();
            String height = heightField.getText();
            String weight = weightField.getText();
            String temperature = temperatureField.getText();
            String healthConcerns = healthConcernsField.getText();
            String healthHistory = healthHistoryField.getText();
            String doctorsNotes = doctorsNotesField.getText();
            String medication = medicationField.getText();

            // Validate input fields
            if (date.isEmpty() || height.isEmpty() || weight.isEmpty() || temperature.isEmpty() || healthConcerns.isEmpty() || healthHistory.isEmpty() || doctorsNotes.isEmpty() || medication.isEmpty()) {
                // Display error message if any field is empty
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill out all fields.");
                alert.showAndWait();
                return;
            }

            // Save appointment details to a file
            saveAppointmentDetails(patientId, date, height, weight, temperature, healthConcerns, healthHistory, doctorsNotes, medication);

            // Close the current stage (New Visit Page)
            newVisitStage.close();
            // Show the user info scene again
            showPatientListScene();
        });

        // Create back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            // Close the current stage (New Visit Page)
            newVisitStage.close();
            // Show the user info scene again
            showPatientListScene();
        });

        // Add buttons to the grid layout
        newVisitLayout.add(saveButton, 0, 8);
        newVisitLayout.add(backButton, 1, 8);

        Scene newVisitScene = new Scene(newVisitLayout, 500, 500);
        newVisitStage.setScene(newVisitScene);
        newVisitStage.setTitle("New Visit");
        newVisitStage.show();
    }



    private void saveAppointmentDetails(String patientId, String date, String height, String weight, String temperature, String healthConcerns, String healthHistory, String doctorsNotes, String medication) {
        // Create the folder for appointment history if it doesn't exist
        File folder = new File(patientId + "_Appointment_History");
        if (!folder.exists()) {
            folder.mkdir();
        }

        // Get the number of existing appointment files for the patient
        File[] existingAppointments = folder.listFiles((dir, name) -> name.startsWith(patientId + "_Appointment"));
        int appointmentNumber = existingAppointments != null ? existingAppointments.length + 1 : 1;

        // Create the file name using patient ID and the next available appointment number
        String fileName = patientId + "_Appointment" + appointmentNumber + ".txt";
        File appointmentFile = new File(folder, fileName);

        try {
            // Check if the appointment file already exists
            if (!appointmentFile.exists()) {
                appointmentFile.createNewFile(); // Create a new file if it doesn't exist
            }

            // Write appointment details to the file
            try (PrintWriter writer = new PrintWriter(new FileWriter(appointmentFile))) {
                writer.println("Date: " + date);
                writer.println("Height: " + height);
                writer.println("Weight: " + weight);
                writer.println("Temperature: " + temperature);
                writer.println("Health Concerns: " + healthConcerns);
                writer.println("Health History: " + healthHistory);
                writer.println("Doctor's Notes: " + doctorsNotes);
                writer.println("Medication: " + medication);
                System.out.println("Appointment details saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    private List<String> getAllPatientIds() {
        List<String> patientIds = new ArrayList<>();
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith("_patient.txt")) {
                    String fileName = file.getName();
                    // Extract patient ID from the file name
                    String patientId = fileName.substring(0, fileName.lastIndexOf('_'));
                    patientIds.add(patientId);
                }
            }
        }
        return patientIds;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
