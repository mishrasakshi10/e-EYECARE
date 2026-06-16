package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;

@WebServlet("/WritePrescriptionsServlet")
public class WritePrescriptionsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // --- GET SESSION ---
        HttpSession session = request.getSession(false); // do NOT create new session

        if (session == null) {
            out.write("{\"status\":\"error\",\"message\":\"Session expired. Please login as doctor.\"}");
            return;
        }

        // Read session attributes from LoginServlet
        Object userIdObj = session.getAttribute("userId"); // matches your LoginServlet
        Object roleObj = session.getAttribute("role");

        if (userIdObj == null || roleObj == null) {
            out.write("{\"status\":\"error\",\"message\":\"Session expired. Please login as doctor.\"}");
            return;
        }

        String role = roleObj.toString();
        if (!"doctor".equalsIgnoreCase(role)) {
            out.write("{\"status\":\"error\",\"message\":\"Access denied: Only doctors can write prescriptions.\"}");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            int doctorId = (int) userIdObj;
            int appointmentId = Integer.parseInt(request.getParameter("appointment_id")); // from form
            String diagnosis = request.getParameter("diagnosis");
            String medicines = request.getParameter("medicines");
            String notes = request.getParameter("notes");

            // --- GET patient_id FROM appointments ---
            String getPatientSql = "SELECT patient_id FROM appointments WHERE id=?";
            PreparedStatement ps1 = con.prepareStatement(getPatientSql);
            ps1.setInt(1, appointmentId);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int patientId = rs.getInt("patient_id");

                // --- INSERT INTO prescriptions ---
                String insertSql = "INSERT INTO prescriptions (doctor_id, patient_id, id, diagnosis, medicines, notes) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement ps2 = con.prepareStatement(insertSql);
                ps2.setInt(1, doctorId);
                ps2.setInt(2, patientId);
                ps2.setInt(3, appointmentId); // <-- store appointment id in `id` column
                ps2.setString(4, diagnosis);
                ps2.setString(5, medicines);
                ps2.setString(6, notes);

                int row = ps2.executeUpdate();
                if (row > 0) {
                    out.write("{\"status\":\"success\",\"message\":\"Prescription added successfully.\"}");
                } else {
                    out.write("{\"status\":\"error\",\"message\":\"Failed to add prescription.\"}");
                }

            } else {
                out.write("{\"status\":\"error\",\"message\":\"Invalid Appointment ID.\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"status\":\"error\",\"message\":\"Error: " + e.getMessage().replace("\"","'") + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("writePrescriptions.html");
    }
}
