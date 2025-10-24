package edu.rmit.cosc1295.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.rmit.cosc1295.model.Appointment;

public class AppointmentDAO {

    // =====================================================
    // 🗄️ DATABASE CONFIGURATION
    // =====================================================
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/appointment"; // ✅ Correct DB name
    private static final String USER = "root";
    private static final String PASSWORD = "R@566432"; // 🔐 Your MySQL password

    public AppointmentDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ JDBC Driver loaded successfully!");
            ensureTableExists();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Failed to load MySQL driver: " + e.getMessage());
        }
    }

    // =====================================================
    // ✅ AUTO-CREATE TABLE IF NOT EXISTS (WITH GENDER)
    // =====================================================
    private void ensureTableExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS appointments (
                id INT AUTO_INCREMENT PRIMARY KEY,
                resident_id INT,
                first_name VARCHAR(100),
                last_name VARCHAR(100),
                age INT,
                gender VARCHAR(20),
                doctor_name VARCHAR(100),
                specialization VARCHAR(100),
                bed_number VARCHAR(50),
                room VARCHAR(50),
                floor VARCHAR(50),
                condition_status VARCHAR(50),
                shift VARCHAR(50),
                appointment_date VARCHAR(20),
                appointment_time VARCHAR(20)
            )
            """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("🧩 Verified/created 'appointments' table (with gender).");
        } catch (SQLException e) {
            System.out.println("⚠️ Failed to verify/create appointments table: " + e.getMessage());
        }
    }

    // =====================================================
    // 🟢 INSERT (SAVE) APPOINTMENT
    // =====================================================
    public void addAppointment(Appointment appointment) {
        String sql = """
                INSERT INTO appointments 
                (resident_id, first_name, last_name, age, gender, doctor_name, specialization,
                 bed_number, room, floor, condition_status, shift, appointment_date, appointment_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointment.getResidentId());
            stmt.setString(2, appointment.getFirstName());
            stmt.setString(3, appointment.getLastName());
            stmt.setInt(4, appointment.getAge());
            stmt.setString(5, appointment.getGender());
            stmt.setString(6, appointment.getDoctorName());
            stmt.setString(7, appointment.getSpecialization());
            stmt.setString(8, appointment.getBedNumber());
            stmt.setString(9, appointment.getRoom());
            stmt.setString(10, appointment.getFloor());
            stmt.setString(11, appointment.getCondition());
            stmt.setString(12, appointment.getShift());
            stmt.setString(13, appointment.getAppointmentDate());
            stmt.setString(14, appointment.getAppointmentTime());

            stmt.executeUpdate();
            System.out.println("✅ Appointment added successfully!");

        } catch (SQLException e) {
            System.out.println("❌ [SQL ERROR] Failed to add appointment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =====================================================
    // 🟡 GET ALL APPOINTMENTS
    // =====================================================
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Appointment a = new Appointment(
                        rs.getInt("id"),
                        rs.getInt("resident_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("age"),
                        rs.getString("gender"), // ✅ Added gender support
                        rs.getString("doctor_name"),
                        rs.getString("specialization"),
                        rs.getString("bed_number"),
                        rs.getString("room"),
                        rs.getString("floor"),
                        rs.getString("condition_status"),
                        rs.getString("shift"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time")
                );
                appointments.add(a);
            }

            System.out.println("✅ Loaded " + appointments.size() + " appointments from DB.");

        } catch (SQLException e) {
            System.out.println("❌ [SQL ERROR] Failed to fetch appointments: " + e.getMessage());
            e.printStackTrace();
        }

        return appointments;
    }

    // =====================================================
    // ✏️ UPDATE APPOINTMENT
    // =====================================================
    public void updateAppointment(Appointment appointment) {
        String sql = """
                UPDATE appointments SET
                    resident_id = ?, first_name = ?, last_name = ?, age = ?, gender = ?, 
                    doctor_name = ?, specialization = ?, bed_number = ?, room = ?, floor = ?, 
                    condition_status = ?, shift = ?, appointment_date = ?, appointment_time = ?
                WHERE id = ?
                """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appointment.getResidentId());
            stmt.setString(2, appointment.getFirstName());
            stmt.setString(3, appointment.getLastName());
            stmt.setInt(4, appointment.getAge());
            stmt.setString(5, appointment.getGender());
            stmt.setString(6, appointment.getDoctorName());
            stmt.setString(7, appointment.getSpecialization());
            stmt.setString(8, appointment.getBedNumber());
            stmt.setString(9, appointment.getRoom());
            stmt.setString(10, appointment.getFloor());
            stmt.setString(11, appointment.getCondition());
            stmt.setString(12, appointment.getShift());
            stmt.setString(13, appointment.getAppointmentDate());
            stmt.setString(14, appointment.getAppointmentTime());
            stmt.setInt(15, appointment.getId());

            stmt.executeUpdate();
            System.out.println("✏️ Appointment updated successfully (ID: " + appointment.getId() + ")");

        } catch (SQLException e) {
            System.out.println("❌ Failed to update appointment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =====================================================
    // 🗑️ DELETE APPOINTMENT
    // =====================================================
    public void deleteAppointment(int id) {
        String sql = "DELETE FROM appointments WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("🗑️ Appointment deleted (ID: " + id + ")");

        } catch (SQLException e) {
            System.out.println("❌ Failed to delete appointment: " + e.getMessage());
        }
    }
}
