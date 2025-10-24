package edu.rmit.cosc1295.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.rmit.cosc1295.dao.AppointmentDAO;
import edu.rmit.cosc1295.model.Appointment;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * Enhanced Dashboard for HealthCare System
 * Displays 8 charts with summary metrics for better staff and doctor management.
 */
public class DashboardController {

    // ─────────── 📊 Charts ───────────
    @FXML private BarChart<String, Number> doctorChart;
    @FXML private PieChart specializationChart;
    @FXML private BarChart<String, Number> shiftChart;
    @FXML private PieChart conditionChart;
    @FXML private BarChart<String, Number> floorChart;
    @FXML private PieChart genderChart;
    @FXML private LineChart<String, Number> trendChart;
    @FXML private StackedBarChart<String, Number> doctorResidentChart; // NEW CHART

    // ─────────── 📋 Summary Labels ───────────
    @FXML private Label totalAppointmentsLabel;
    @FXML private Label activeDoctorsLabel;
    @FXML private Label occupiedBedsLabel;
    @FXML private Label todayAppointmentsLabel;
    @FXML private Label criticalPatientsLabel;

    // ─────────── ⚙️ Data ───────────
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private List<Appointment> allAppointments = new ArrayList<>();

    @FXML
    public void initialize() {
        System.out.println("📊 Initializing Dashboard...");
        loadDashboardData();
    }

    // ==============================================================
    // 🔁 LOAD DASHBOARD DATA
    // ==============================================================
    private void loadDashboardData() {
        try {
            allAppointments = appointmentDAO.getAllAppointments();
            if (allAppointments.isEmpty()) {
                System.out.println("⚠️ No appointment data found in database.");
                return;
            }

            // --- Summary Cards ---
            setSummaryCards();

            // --- Charts ---
            setupDoctorChart();
            setupSpecializationChart();
            setupShiftChart();
            setupConditionChart();
            setupFloorChart();
            setupGenderChart();
            setupTrendChart();
            setupDoctorResidentChart(); // ✅ NEW CHART

            fadeInCharts();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to load dashboard data: " + e.getMessage());
        }
    }

    // ==============================================================
    // 🧮 SUMMARY CARDS
    // ==============================================================
    private void setSummaryCards() {
        totalAppointmentsLabel.setText(String.valueOf(allAppointments.size()));

        long activeDoctors = allAppointments.stream()
                .map(Appointment::getDoctorName)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        activeDoctorsLabel.setText(String.valueOf(activeDoctors));

        long occupiedBeds = allAppointments.stream()
                .map(Appointment::getBedNumber)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        occupiedBedsLabel.setText(String.valueOf(occupiedBeds));

        long todayAppointments = allAppointments.stream()
                .filter(a -> a.getAppointmentDate() != null &&
                        a.getAppointmentDate().equals(LocalDate.now().toString()))
                .count();
        todayAppointmentsLabel.setText(String.valueOf(todayAppointments));

        long criticalPatients = allAppointments.stream()
                .filter(a -> a.getCondition() != null &&
                        a.getCondition().equalsIgnoreCase("Critical"))
                .count();
        criticalPatientsLabel.setText(String.valueOf(criticalPatients));
    }

    // ==============================================================
    // 🩺 CHARTS SETUP
    // ==============================================================
    private void setupDoctorChart() {
        Map<String, Long> data = groupCount(Appointment::getDoctorName);
        doctorChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Appointments per Doctor");
        data.forEach((doctor, count) -> series.getData().add(new XYChart.Data<>(doctor, count)));
        doctorChart.getData().add(series);
    }

    private void setupSpecializationChart() {
        Map<String, Long> data = groupCount(Appointment::getSpecialization);
        specializationChart.setData(toPieData(data));
        specializationChart.setTitle("Appointments by Specialization");
    }

    private void setupShiftChart() {
        Map<String, Long> data = groupCount(Appointment::getShift);
        shiftChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Appointments by Shift");
        data.forEach((shift, count) -> series.getData().add(new XYChart.Data<>(shift, count)));
        shiftChart.getData().add(series);
    }

    private void setupConditionChart() {
        Map<String, Long> data = groupCount(Appointment::getCondition);
        conditionChart.setData(toPieData(data));
        conditionChart.setTitle("Patient Condition");
    }

    private void setupFloorChart() {
        Map<String, Long> data = groupCount(Appointment::getFloor);
        floorChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Appointments by Floor");
        data.forEach((floor, count) -> series.getData().add(new XYChart.Data<>(floor, count)));
        floorChart.getData().add(series);
    }

    private void setupGenderChart() {
        Map<String, Long> data = groupCount(Appointment::getGender);
        genderChart.setData(toPieData(data));
        genderChart.setTitle("Gender Distribution");
    }

    private void setupTrendChart() {
        Map<String, Long> data = groupCount(Appointment::getAppointmentDate)
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));

        trendChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Appointments Trend");
        data.forEach((date, count) -> series.getData().add(new XYChart.Data<>(date, count)));
        trendChart.getData().add(series);
    }

    // ==============================================================
    // 8️⃣ DOCTOR–RESIDENT RELATIONSHIP CHART
    // ==============================================================
    private void setupDoctorResidentChart() {
        doctorResidentChart.getData().clear();

        Map<String, Map<String, Long>> shiftDoctorResidentCount = allAppointments.stream()
                .collect(Collectors.groupingBy(
                        Appointment::getShift,
                        Collectors.groupingBy(Appointment::getDoctorName, Collectors.counting())
                ));

        for (Map.Entry<String, Map<String, Long>> shiftEntry : shiftDoctorResidentCount.entrySet()) {
            String shift = shiftEntry.getKey();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(shift);

            for (Map.Entry<String, Long> doctorEntry : shiftEntry.getValue().entrySet()) {
                String doctor = doctorEntry.getKey();
                series.getData().add(new XYChart.Data<>(doctor, doctorEntry.getValue()));
            }

            doctorResidentChart.getData().add(series);
        }

        // Add tooltips to show Resident name + ID
        for (XYChart.Series<String, Number> series : doctorResidentChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                String doctor = data.getXValue();
                String shift = series.getName();

                List<Appointment> related = allAppointments.stream()
                        .filter(a -> a.getDoctorName().equals(doctor)
                                && a.getShift().equals(shift))
                        .toList();

                String tooltipText = related.stream()
                        .map(a -> String.format("Resident: %s %s (ID: %d)",
                                a.getFirstName(), a.getLastName(), a.getResidentId()))
                        .collect(Collectors.joining("\n"));

                Tooltip tooltip = new Tooltip("Doctor: " + doctor + "\nShift: " + shift + "\n" + tooltipText);
                Tooltip.install(data.getNode(), tooltip);
            }
        }

        doctorResidentChart.setTitle("Doctor–Resident–Shift Allocation Overview");
    }

    // ==============================================================
    // 🧩 HELPER METHODS
    // ==============================================================
    private Map<String, Long> groupCount(Function<Appointment, String> classifier) {
        return allAppointments.stream()
                .map(classifier)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
    }

    private ObservableList<PieChart.Data> toPieData(Map<String, Long> data) {
        return FXCollections.observableArrayList(
                data.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey() + " (" + e.getValue() + ")", e.getValue()))
                        .toList()
        );
    }

    private void fadeInCharts() {
        List<Chart> charts = Arrays.asList(
                doctorChart, specializationChart, shiftChart, conditionChart,
                floorChart, genderChart, trendChart, doctorResidentChart
        );
        for (Chart chart : charts) {
            FadeTransition fade = new FadeTransition(Duration.millis(600), chart);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    // ==============================================================
    // 🔁 REFRESH BUTTON
    // ==============================================================
    @FXML
    private void onRefreshData() {
        loadDashboardData();
    }
}
