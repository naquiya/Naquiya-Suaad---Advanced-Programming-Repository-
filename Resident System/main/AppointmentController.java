package edu.rmit.cosc1295.app;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.rmit.cosc1295.dao.AppointmentDAO;
import edu.rmit.cosc1295.model.Appointment;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class AppointmentController {

    // ====== FORM FIELDS ======
    @FXML private TextField residentIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private ComboBox<String> doctorCombo;
    @FXML private ComboBox<String> specializationCombo;
    @FXML private ComboBox<String> bedCombo;
    @FXML private ComboBox<String> roomCombo;
    @FXML private ComboBox<String> floorCombo;
    @FXML private ComboBox<String> conditionCombo;
    @FXML private ComboBox<String> shiftCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TextField searchField;

    // ====== TABLE ======
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> idColumn;
    @FXML private TableColumn<Appointment, Integer> residentIdColumn;
    @FXML private TableColumn<Appointment, String> firstNameColumn;
    @FXML private TableColumn<Appointment, String> lastNameColumn;
    @FXML private TableColumn<Appointment, Integer> ageColumn;
    @FXML private TableColumn<Appointment, String> genderColumn;
    @FXML private TableColumn<Appointment, String> doctorColumn;
    @FXML private TableColumn<Appointment, String> specializationColumn;
    @FXML private TableColumn<Appointment, String> bedColumn;
    @FXML private TableColumn<Appointment, String> roomColumn;
    @FXML private TableColumn<Appointment, String> floorColumn;
    @FXML private TableColumn<Appointment, String> conditionColumn;
    @FXML private TableColumn<Appointment, String> shiftColumn;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> timeColumn;

    // ====== DATA SOURCES ======
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final Map<String, String> doctorSpecializationMap = new HashMap<>();
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    // ====== INITIALIZE ======
    @FXML
    public void initialize() {
        System.out.println("🩺 Initializing AppointmentController...");

        setupDoctorSpecializations();
        setupGenderOptions();
        setupBedOptions();
        setupRoomFloorOptions();
        setupConditionOptions();
        setupShiftOptions();
        setupTableColumns();

        loadAppointments();
    }

    // ====== PRELOADERS ======
    //List of Doctors and their value is set to their respective department

    private void setupDoctorSpecializations() {
        doctorSpecializationMap.put("Dr. A. Mehta", "Cardiology");
        doctorSpecializationMap.put("Dr. S. Verma", "Neurology");
        doctorSpecializationMap.put("Dr. R. Patel", "Orthopedics");
        doctorSpecializationMap.put("Dr. L. Rao", "Dermatology");
        doctorSpecializationMap.put("Dr. K. Nair", "Pediatrics");
        doctorSpecializationMap.put("Dr. T. Sharma", "Psychiatry");

        doctorCombo.setItems(FXCollections.observableArrayList(doctorSpecializationMap.keySet()));
        specializationCombo.setItems(FXCollections.observableArrayList(
                doctorSpecializationMap.values().stream().distinct().toList()
        ));
    }

    private void setupGenderOptions() {
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
    }

    //Keeping the count of the number of beds to about 20 and not less than 1 bed
    private void setupBedOptions() {
        bedCombo.setItems(FXCollections.observableArrayList(
            IntStream.rangeClosed(1, 20)
                     .mapToObj(i -> "Bed " + i)
                     .collect(Collectors.toList())
        ));
    }
    
    //Number of Rooms and Floors, later also setting the condition of the patient using Setup conditions
    private void setupRoomFloorOptions() {
        roomCombo.setItems(FXCollections.observableArrayList(
            "Room 101", "Room 102", "Room 103", "Room 201", "Room 202"
        ));

        floorCombo.setItems(FXCollections.observableArrayList(
            "Floor 1", "Floor 2", "Floor 3"
        ));
    }

    private void setupConditionOptions() {
        conditionCombo.setItems(FXCollections.observableArrayList(
            "Normal", "Good", "Fair", "Serious", "Critical"
        ));
    }

    private void setupShiftOptions() {
        shiftCombo.setItems(FXCollections.observableArrayList(
            "Morning (06:00 - 14:00)",
            "Evening (14:00 - 22:00)",
            "Night (22:00 - 06:00)"
        ));
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
        residentIdColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getResidentId()).asObject());
        firstNameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastName()));
        ageColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getAge()).asObject());
        genderColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGender()));
        doctorColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDoctorName()));
        specializationColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSpecialization()));
        bedColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBedNumber()));
        roomColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRoom()));
        floorColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFloor()));
        conditionColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCondition()));
        shiftColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getShift()));
        dateColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAppointmentDate()));
        timeColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAppointmentTime()));
    }

    // ====== SELECTION HANDLERS ======

    @FXML
    private void handleDoctorSelection() {
        String doctor = doctorCombo.getValue();
        if (doctor != null && doctorSpecializationMap.containsKey(doctor)) {
            specializationCombo.setValue(doctorSpecializationMap.get(doctor));
        }
    }

    @FXML
    private void handleSpecializationSelection() {
        String specialization = specializationCombo.getValue();
        if (specialization != null) {
            ObservableList<String> filteredDoctors = FXCollections.observableArrayList();
            for (Map.Entry<String, String> entry : doctorSpecializationMap.entrySet()) {
                if (entry.getValue().equals(specialization)) filteredDoctors.add(entry.getKey());
            }
            doctorCombo.setItems(filteredDoctors);
        }
    }

    // ====== SAVE APPOINTMENT ======
    @FXML
    private void handleSaveAppointment() {
        try {
            int residentId = Integer.parseInt(residentIdField.getText());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String gender = genderCombo.getValue();
            String doctor = doctorCombo.getValue();
            String specialization = specializationCombo.getValue();
            String bed = bedCombo.getValue();
            String room = roomCombo.getValue();
            String floor = floorCombo.getValue();
            String condition = conditionCombo.getValue();
            String shift = shiftCombo.getValue();
            LocalDate date = datePicker.getValue();
            String time = timeField.getText();

            if (firstName.isEmpty() || lastName.isEmpty() || gender == null || doctor == null || specialization == null
                    || bed == null || room == null || floor == null || condition == null
                    || shift == null || date == null || time.isEmpty()) {
                showAlert(AlertType.WARNING, "Missing Fields", "Please fill in all required fields including gender.");
                return;
            }

            Appointment appointment = new Appointment(0, residentId, firstName, lastName, age, gender,
                    doctor, specialization, bed, room, floor, condition, shift,
                    date.toString(), time);

            appointmentDAO.addAppointment(appointment);
            loadAppointments();
            clearForm();
            showAlert(AlertType.INFORMATION, "Success", "Appointment saved successfully!");

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Input", "Resident ID and Age must be numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to save appointment: " + e.getMessage());
        }
    }

    // ====== UPDATE APPOINTMENT ======
    @FXML
    private void handleUpdateAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an appointment to update.");
            return;
        }

        try {
            selected.setFirstName(firstNameField.getText());
            selected.setLastName(lastNameField.getText());
            selected.setAge(Integer.parseInt(ageField.getText()));
            selected.setGender(genderCombo.getValue());
            selected.setDoctorName(doctorCombo.getValue());
            selected.setSpecialization(specializationCombo.getValue());
            selected.setBedNumber(bedCombo.getValue());
            selected.setRoom(roomCombo.getValue());
            selected.setFloor(floorCombo.getValue());
            selected.setCondition(conditionCombo.getValue());
            selected.setShift(shiftCombo.getValue());
            selected.setAppointmentDate(datePicker.getValue().toString());
            selected.setAppointmentTime(timeField.getText());

            appointmentDAO.updateAppointment(selected);
            loadAppointments();
            clearForm();

            showAlert(Alert.AlertType.INFORMATION, "Updated", "Appointment updated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update appointment: " + e.getMessage());
        }
    }
    //Exception handling for errors or incorrect entries made, shows failed to update details if incorrect type of data is entered

    // ====== DELETE APPOINTMENT ======
    @FXML
    private void handleDeleteAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an appointment to delete.");
            return;
        }

        try {
            appointmentDAO.deleteAppointment(selected.getId());
            loadAppointments();
            clearForm();

            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Appointment deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete appointment: " + e.getMessage());
        }
    }

    // ====== REFRESH APPOINTMENT LIST ======
    //Keeping a button to refresh appointment or just to refresh the page
    @FXML
    private void handleRefreshAppointments() {
        loadAppointments();
        showAlert(Alert.AlertType.INFORMATION, "Refreshed", "Appointment list reloaded successfully!");
    }

    // ====== SEARCH ======
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            appointmentTable.setItems(allAppointments);
            return;
        }

        List<Appointment> filtered = allAppointments.stream()
                .filter(a -> a.getFirstName().toLowerCase().contains(keyword)
                        || a.getDoctorName().toLowerCase().contains(keyword)
                        || a.getAppointmentDate().toLowerCase().contains(keyword)
                        || a.getGender().toLowerCase().contains(keyword))
                .collect(Collectors.toList());

        appointmentTable.setItems(FXCollections.observableArrayList(filtered));

        FadeTransition fade = new FadeTransition(Duration.millis(300), appointmentTable);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    // ====== UTILS ======

    private void loadAppointments() {
        try {
            allAppointments = FXCollections.observableArrayList(appointmentDAO.getAllAppointments());
            appointmentTable.setItems(allAppointments);
        } catch (Exception e) {
            System.out.println("❌ Error loading appointments: " + e.getMessage());
        }
    }

   //Exception handling for error loading an appointment if its not present in the list of all appointments
   
    private void clearForm() {
        residentIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        ageField.clear();
        genderCombo.setValue(null);
        doctorCombo.setValue(null);
        specializationCombo.setValue(null);
        bedCombo.setValue(null);
        roomCombo.setValue(null);
        floorCombo.setValue(null);
        conditionCombo.setValue(null);
        shiftCombo.setValue(null);
        datePicker.setValue(null);
        timeField.clear();
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
