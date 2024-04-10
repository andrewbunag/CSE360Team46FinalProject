package phase3;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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

    private String currentUserId; 
    private boolean isStaffClicked = false; 


    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        
        Button staffButton = new Button("Staff");
        Button patientButton = new Button("Patient");
        

       
        patientButton.setOnAction(e -> {
            isStaffClicked = false; 
            showLoginOrSignUpScene();
        });
        staffButton.setOnAction(e -> {
            isStaffClicked = true; 
            showLoginOrSignUpScene();
        });

        HBox userSelectionLayout = new HBox(10);
        userSelectionLayout.setPadding(new Insets(20));
        userSelectionLayout.setAlignment(Pos.CENTER);
        userSelectionLayout.getChildren().addAll(staffButton, patientButton);

        VBox loginOrSignUpLayout = new VBox(10);
        loginOrSignUpLayout.setPadding(new Insets(20));
        loginOrSignUpLayout.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Login");
        Button signUpButton = new Button("Sign Up");

        loginButton.setOnAction(e -> showLoginScene());

        signUpButton.setOnAction(e -> showSignUpDialog());

        loginOrSignUpLayout.getChildren().addAll(loginButton, signUpButton);

        userSelectionScene = new Scene(userSelectionLayout, 500, 500);
        loginOrSignUpScene = new Scene(loginOrSignUpLayout, 500, 500);

        primaryStage.setTitle("Hospital User Selection");
        primaryStage.setScene(userSelectionScene);
        primaryStage.show();
    }
    //login/sign up
    private void showLoginOrSignUpScene() {
        primaryStage.setScene(loginOrSignUpScene);
    }

    private void showSignUpDialog() {
        Dialog<String> signUpDialog = new Dialog<>();
        signUpDialog.setTitle("Create Account");

        Label nameLabel = new Label("Name:");
        Label passwordLabel = new Label("Password:");
        Label dobLabel = new Label("Date of Birth:");
        Label sexLabel = new Label("Sex:");
        TextField nameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField dobField = new TextField(); 
        TextField sexField = new TextField();

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.getChildren().addAll(nameLabel, nameField, passwordLabel, passwordField, dobLabel, dobField, sexLabel, sexField);
        signUpDialog.getDialogPane().setContent(dialogLayout);

        ButtonType signUpButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        signUpDialog.getDialogPane().getButtonTypes().addAll(signUpButtonType, ButtonType.CANCEL);

        signUpDialog.setResultConverter(dialogButton -> {
            if (dialogButton == signUpButtonType) {
                String name = nameField.getText();
                String password = passwordField.getText();
                String dob = dobField.getText();
                String sex = sexField.getText();
                String id = generateRandomID();
                currentUserId = id; 
                String accountInfo = name + "," + id + "," + password + "," + dob + "," + sex + "," + (isStaffClicked ? "Staff" : "Patient");
                saveAccountInfo(accountInfo, id); 
                return "Signed Up";
            }
            return null;
        });

        signUpDialog.showAndWait();
    }
    //generates patient ID
    private String generateRandomID() {
        Random random = new Random();
        int id = random.nextInt(90000) + 10000;
        return String.valueOf(id);
    }
    
    //saves new users in files
    private void saveAccountInfo(String accountInfo, String id) {
        String fileName = id + ".txt";
        try {
            
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
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Name:");
        Label passwordLabel = new Label("Password:");
        TextField nameField = new TextField();
        PasswordField passwordField = new PasswordField();

        loginLayout.getChildren().addAll(nameLabel, nameField, passwordLabel, passwordField);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String name = nameField.getText();
            String password = passwordField.getText();
            if (verifyLogin(name, password)) {
                System.out.println("Login successful");
            } else {
                System.out.println("Invalid credentials");
            }
        });

        loginLayout.getChildren().add(loginButton);

        loginScene = new Scene(loginLayout, 500, 500);
        primaryStage.setScene(loginScene);
    }

//checks login credentials by going through files
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
                                currentUserId = parts[1]; 
                                if (isStaffClicked && parts[5].equals("Staff")) {
                                    showPatientListScene();
                                } else if (!isStaffClicked && parts[5].equals("Patient")) {
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
        Map<String, String> userInfo = readUserInfo(userId);

        Label nameLabel = new Label("Name: " + userInfo.get("Name"));
        Label dobLabel = new Label("Date of Birth: " + userInfo.get("DOB"));
        Label sexLabel = new Label("Sex: " + userInfo.get("Sex"));

        String insuranceInfo = readInsuranceInfo(userId);
        Label insuranceLabel = new Label("Insurance Info: " + insuranceInfo);

        String pharmacyInfo = readPharmacyInfo(userId);
        Label pharmacyLabel = new Label("Pharmacy Info: " + pharmacyInfo);

        Button editInsuranceButton = new Button("Edit Insurance Info");
        Button editPharmacyButton = new Button("Edit Pharmacy Info");

        editInsuranceButton.setOnAction(e -> showEditInsuranceDialog(userId));
        editPharmacyButton.setOnAction(e -> showEditPharmacyDialog(userId));

        Button messageButton = new Button("Message Clinic");
        messageButton.setOnAction(e -> Messages(userId, "Patient"));
        
        Button visitsButton = new Button("Visit History");
        visitsButton.setOnAction(event -> {
            if(numOfAppointments(userId) > 0)
            	VisitPages(userId);
            else
            	System.out.println("No Visits");
            
        });
        VBox userInfoLayout = new VBox(10);
        userInfoLayout.setPadding(new Insets(20));
        userInfoLayout.setAlignment(Pos.CENTER);
        userInfoLayout.getChildren().addAll(nameLabel, dobLabel, sexLabel, insuranceLabel, pharmacyLabel, editInsuranceButton, editPharmacyButton, visitsButton, messageButton);

        userInfoScene = new Scene(userInfoLayout, 500, 500);

        primaryStage.setTitle("User Information");
        primaryStage.setScene(userInfoScene);
    }

    //allows user to edit insurance
    private void showEditInsuranceDialog(String userId) {
        Dialog<String> editInsuranceDialog = new Dialog<>();
        editInsuranceDialog.setTitle("Edit Insurance Info");

        Label companyLabel = new Label("Insurance Company:");
        Label idLabel = new Label("Insurance ID:");
        TextField companyField = new TextField();
        TextField idField = new TextField();

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.getChildren().addAll(companyLabel, companyField, idLabel, idField);
        editInsuranceDialog.getDialogPane().setContent(dialogLayout);

        ButtonType editInsuranceButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        editInsuranceDialog.getDialogPane().getButtonTypes().addAll(editInsuranceButtonType, ButtonType.CANCEL);

        editInsuranceDialog.setResultConverter(dialogButton -> {
            if (dialogButton == editInsuranceButtonType) {
                String company = companyField.getText();
                String id = idField.getText();
                String insuranceInfo = "Insurance Company: " + company + "\nInsurance ID: " + id;
                appendInsuranceInfo(insuranceInfo, userId);
                showUserInfoScene(userId);
                return "Saved";
            }
            return null;
        });

        editInsuranceDialog.showAndWait();
    }
    //allows user to edit Pharmacy info
    private void showEditPharmacyDialog(String userId) {
        Dialog<String> editPharmacyDialog = new Dialog<>();
        editPharmacyDialog.setTitle("Edit Pharmacy Info");

        Label nameLabel = new Label("Pharmacy Name:");
        Label addressLabel = new Label("Pharmacy Address:");
        TextField nameField = new TextField();
        TextField addressField = new TextField();

        VBox dialogLayout = new VBox(10);
        dialogLayout.setPadding(new Insets(20));
        dialogLayout.getChildren().addAll(nameLabel, nameField, addressLabel, addressField);
        editPharmacyDialog.getDialogPane().setContent(dialogLayout);

        ButtonType editPharmacyButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        editPharmacyDialog.getDialogPane().getButtonTypes().addAll(editPharmacyButtonType, ButtonType.CANCEL);

        editPharmacyDialog.setResultConverter(dialogButton -> {
            if (dialogButton == editPharmacyButtonType) {
                String name = nameField.getText();
                String address = addressField.getText();
                String pharmacyInfo = "Pharmacy Name: " + name + "\nPharmacy Address: " + address;
                appendPharmacyInfo(pharmacyInfo, userId);
                showUserInfoScene(userId);
                return "Saved";
            }
            return null;
        });

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
        String fileName = userId + "_patient.txt"; 
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 6) { 
                    userInfo.put("Name", parts[0]);
                    
                    userInfo.put("DOB", parts[3]);
                    userInfo.put("Sex", parts[4]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfo;
    }


    //displays insurance info
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
    //displays pharmacy info
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


    //shows full patient list to the doctor side
    private void showPatientListScene() {
        List<String> patientIds = getAllPatientIds();

        ListView<String> patientListView = new ListView<>();
        patientListView.setCellFactory(param -> new ListCell<String>() {
            private final Button NewVisitButton = new Button("New Visit");
            private final Button VisitsButton = new Button("Visits");
            private final Button MessageButton = new Button("Message");
            private final HBox hbox = new HBox(NewVisitButton, VisitsButton, MessageButton);

            {
                NewVisitButton.setOnAction(event -> {
                    String patientId = getItem();
                    NewVisitView(patientId);
                    
                });

                MessageButton.setOnAction(event -> {
                    String patientId = getItem();
                    Messages(patientId, "Staff");
                    
                });
                
                VisitsButton.setOnAction(event -> {
                    String patientId = getItem();
                    if(numOfAppointments(patientId) > 0)
                    	VisitPages(patientId);
                    else
                    	System.out.println("No Visits");
                    
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

       
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showLoginScene());

        VBox patientListLayout = new VBox(10);
        patientListLayout.setPadding(new Insets(20));
        patientListLayout.setAlignment(Pos.CENTER);
        patientListLayout.getChildren().addAll(new Label("List of Patients:"), patientListView, backButton);

        patientListScene = new Scene(patientListLayout, 500, 500);

        primaryStage.setTitle("Patient List");
        primaryStage.setScene(patientListScene);
    }
    
    //Visit summary
    private void Visits(String patientId, int nAppointment)
    {
        Map<String, String> appointInfo = readVisitInfo(patientId, nAppointment);
    	Label topLabel = new Label(appointInfo.get("Date")+ " Visit Summary");
    	Label vitalLabel = new Label("Vitals:");
    	Label height = new Label("Height: " + appointInfo.get("Height"));
    	Label weight = new Label("Height: " + appointInfo.get("Weight"));
    	Label bodyTemp = new Label("Temperature: " + appointInfo.get("Temperature"));
    	Label healthLabel = new Label("Health Concerns: " + appointInfo.get("Concerns"));
		Label medHist = new Label("History: " + appointInfo.get("History"));
		Label docNotes = new Label("Doctor Notes:");
		Label notes = new Label("Notes: " + appointInfo.get("Notes"));
		Label prescriptions = new Label("Prescriptions: " + appointInfo.get("Medication"));
		
		VBox vboxVitals = new VBox(40); 
        vboxVitals.setPadding(new Insets(40)); 
        vboxVitals.getChildren().addAll(vitalLabel,height, weight, bodyTemp);
        vboxVitals.setAlignment(Pos.TOP_LEFT);
        vboxVitals.setStyle("-fx-background-color: lightgray;");
        
        VBox vboxHealth = new VBox(40);
        vboxHealth.setPadding(new Insets(40)); 
        vboxHealth.getChildren().addAll(healthLabel);
        vboxHealth.setAlignment(Pos.TOP_RIGHT);
        vboxHealth.setStyle("-fx-background-color: lightgray;");
        
        VBox vboxMed = new VBox(40);
        vboxMed.setPadding(new Insets(40)); 
        vboxMed.getChildren().addAll(medHist);
        vboxMed.setAlignment(Pos.TOP_CENTER);
        vboxMed.setStyle("-fx-background-color: lightgray;");
		        
		HBox hbox1 = new HBox(10);
        hbox1.getChildren().addAll(topLabel,vboxVitals,vboxHealth,vboxMed);
        

        VBox vboxDoc = new VBox(40);
        vboxDoc.setPadding(new Insets(40)); 
        vboxDoc.getChildren().addAll(docNotes, notes, prescriptions);
        vboxDoc.setAlignment(Pos.BOTTOM_LEFT);
        vboxDoc.setStyle("-fx-background-color: lightblue;");
        Insets margins = new Insets(20, 20, 20, 20); 
        
        VBox page = new VBox(40);
        
        page.setMargin(vboxDoc, margins);
        page.setPadding(new Insets(10, 10, 10, 10));
        page.getChildren().addAll(topLabel,hbox1,vboxDoc);
        page.setStyle("-fx-background-color: lavender;");
        
        Scene visitsScene = new Scene(page, 750, 700);
        Stage visitsStage = new Stage();
        visitsStage.setScene(visitsScene);
        visitsStage.setTitle("Visits with Patient " + patientId);
        visitsStage.show();
    }
    
    
    //select which visit summary you want to see details of
	private void VisitPages(String patientId) {
		int nAppointments = numOfAppointments(patientId);

		Label labelTop = new Label("Visit Summaries");
		labelTop.setTextFill(Color.BLACK);
		labelTop.setFont(Font.font(null, 18));

		primaryStage.setTitle("Visit Summaries");

		ComboBox comboBox = new ComboBox();
		for (int i = nAppointments; i >= 1; i--) {
			comboBox.getItems().add("Appointment " + i + " Date: " + visitDate(patientId, i));
		}
		Button btn = new Button();
		int n = comboBox.getSelectionModel().getSelectedIndex();
		btn.setText("Confirm Visit Summary");
		btn.setOnAction(event -> {
			int selectedBox = comboBox.getSelectionModel().getSelectedIndex();
			if (selectedBox > -1) {
				selectedBox = nAppointments - selectedBox;
				Visits(patientId, selectedBox);
			}
			
		});

		StackPane root = new StackPane();
		root.setPadding(new Insets(20, 20, 20, 20));
		StackPane.setAlignment(labelTop, javafx.geometry.Pos.TOP_CENTER);
		StackPane.setAlignment(btn, Pos.BOTTOM_CENTER);
		root.getChildren().addAll(labelTop, comboBox, btn);
		primaryStage.setScene(new Scene(root, 500, 500));
		primaryStage.show();
	}
    
	private Map<String, String> readVisitInfo(String patientId, int nAppointment) {
		Map<String, String> userInfo = new HashMap<>();
		File folder = new File(patientId + "_Appointment_History");
		String fileName = patientId + "_Appointment" + nAppointment + ".txt";
		File appointmentFile = new File(folder, fileName);
		String[] data = new String[8];
		int i = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(appointmentFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.substring(line.lastIndexOf(":") + 2); 
				data[i] = line;
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		userInfo.put("Date", data[0]);
		userInfo.put("Height", data[1]);
		userInfo.put("Weight", data[2]);
		userInfo.put("Temperature", data[3]);
		userInfo.put("Concerns", data[4]);
		userInfo.put("History", data[5]);
		userInfo.put("Notes", data[6]);
		userInfo.put("Medication", data[7]);

		return userInfo;
	}

	private int numOfAppointments(String patientId) {
		int n = 0;
		File folder = new File(patientId + "_Appointment_History");
		File[] existingAppointments = folder.listFiles((dir, name) -> name.startsWith(patientId + "_Appointment"));
		if (existingAppointments != null)
			n = existingAppointments.length;
		return n;
	}
	
	
    private String visitDate(String patientId, int nAppointment)
    {
    	File folder = new File(patientId + "_Appointment_History");
    	String fileName = patientId + "_Appointment" + nAppointment + ".txt";
        File appointmentFile = new File(folder, fileName);
        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(appointmentFile))) {
            line = reader.readLine();
            line = line.substring(line.lastIndexOf(":")+ 2); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
        
    }
    //messages functionality
    private void Messages(String patientId, String Sender) {
        File messageHistoryFile = getMessageHistoryFile(patientId);

        List<String> messageHistory = readMessageHistory(messageHistoryFile);

        VBox messageHistoryLayout = createMessageHistoryLayout(messageHistory);
        TextField messageInputField = new TextField();
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String message = messageInputField.getText();
            if (!message.isEmpty()) {
                sendMessage(messageHistoryFile, message, Sender);
                messageHistory.add(Sender+": " + message); 
                messageHistoryLayout.getChildren().add(new Label(Sender+": " + message)); 
                messageInputField.clear(); 
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
        
        
        VBox messagesLayout = new VBox(10);
        messagesLayout.setAlignment(Pos.TOP_CENTER);
        messagesLayout.getChildren().addAll(messageHistoryLayout, messageInputField, sendButton);
        Scene messagesScene = new Scene(messagesLayout, 500, 500);

        Stage messagesStage = new Stage();
        messagesStage.setScene(messagesScene);
        messagesStage.setTitle("Messages with Patient " + patientId);
        messagesStage.show();
    }

    private File getMessageHistoryFile(String patientId) {
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
            e.printStackTrace();
        }
        return messageHistory;
    }

    private void sendMessage(File messageHistoryFile, String message, String sender) {
        try (FileWriter writer = new FileWriter(messageHistoryFile, true)) {
            writer.write(sender + ": " + message + "\n"); 
        } catch (IOException e) {
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

    //new visit functionality
    private void NewVisitView(String patientId) {
        Stage newVisitStage = new Stage();
        GridPane newVisitLayout = new GridPane();
        newVisitLayout.setAlignment(Pos.CENTER);
        newVisitLayout.setHgap(10);
        newVisitLayout.setVgap(10);
        newVisitLayout.setPadding(new Insets(20));

        Label dateLabel = new Label("Date:");
        Label heightLabel = new Label("Height:");
        Label weightLabel = new Label("Weight:");
        Label temperatureLabel = new Label("Temperature:");
        Label healthConcernsLabel = new Label("Health Concerns:");
        Label healthHistoryLabel = new Label("Health History:");
        Label doctorsNotesLabel = new Label("Doctor's Notes:");
        Label medicationLabel = new Label("Medication:");

        TextField dateField = new TextField();
        TextField heightField = new TextField();
        TextField weightField = new TextField();
        TextField temperatureField = new TextField();
        TextField healthConcernsField = new TextField();
        TextField healthHistoryField = new TextField();
        TextField doctorsNotesField = new TextField();
        TextField medicationField = new TextField();

        newVisitLayout.addColumn(0, dateLabel, heightLabel, weightLabel, temperatureLabel, healthConcernsLabel, healthHistoryLabel, doctorsNotesLabel, medicationLabel);
        newVisitLayout.addColumn(1, dateField, heightField, weightField, temperatureField, healthConcernsField, healthHistoryField, doctorsNotesField, medicationField);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            String date = dateField.getText();
            String height = heightField.getText();
            String weight = weightField.getText();
            String temperature = temperatureField.getText();
            String healthConcerns = healthConcernsField.getText();
            String healthHistory = healthHistoryField.getText();
            String doctorsNotes = doctorsNotesField.getText();
            String medication = medicationField.getText();

            if (date.isEmpty() || height.isEmpty() || weight.isEmpty() || temperature.isEmpty() || healthConcerns.isEmpty() || healthHistory.isEmpty() || doctorsNotes.isEmpty() || medication.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please fill out all fields.");
                alert.showAndWait();
                return;
            }

            saveAppointmentDetails(patientId, date, height, weight, temperature, healthConcerns, healthHistory, doctorsNotes, medication);

            newVisitStage.close();
            showPatientListScene();
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            newVisitStage.close();
            showPatientListScene();
        });

        newVisitLayout.add(saveButton, 0, 8);
        newVisitLayout.add(backButton, 1, 8);

        Scene newVisitScene = new Scene(newVisitLayout, 500, 500);
        newVisitStage.setScene(newVisitScene);
        newVisitStage.setTitle("New Visit");
        newVisitStage.show();
    }



    private void saveAppointmentDetails(String patientId, String date, String height, String weight, String temperature, String healthConcerns, String healthHistory, String doctorsNotes, String medication) {
        File folder = new File(patientId + "_Appointment_History");
        if (!folder.exists()) {
            folder.mkdir();
        }

        File[] existingAppointments = folder.listFiles((dir, name) -> name.startsWith(patientId + "_Appointment"));
        int appointmentNumber = existingAppointments != null ? existingAppointments.length + 1 : 1;

        String fileName = patientId + "_Appointment" + appointmentNumber + ".txt";
        File appointmentFile = new File(folder, fileName);

        try {
            if (!appointmentFile.exists()) {
                appointmentFile.createNewFile(); 
            }

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
