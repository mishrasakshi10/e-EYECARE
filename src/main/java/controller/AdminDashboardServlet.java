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

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // 🔐 ADMIN SECURITY CHECK
        if (session == null ||
            session.getAttribute("userId") == null ||
            !"admin".equals(session.getAttribute("role"))) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int pendingDoctors = 0;
        int todaysAppointments = 0;
        int totalPatients = 0;
        int totalPrescriptions = 0;
        String adminName = "Admin";

        try (Connection conn = DBConnection.getConnection()) {

            // 🔹 Get Admin Name
            String sqlAdmin = "SELECT name FROM user WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlAdmin)) {

                ps.setInt(1, (int) session.getAttribute("userId"));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        adminName = rs.getString("name");
                    }
                }
            }

            // 🔹 Pending Doctors
            String sqlPending = "SELECT COUNT(*) FROM user WHERE role='doctor' AND status='pending'";
            try (PreparedStatement ps = conn.prepareStatement(sqlPending);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    pendingDoctors = rs.getInt(1);
                }
            }

            // 🔹 Today's Appointments
            String sqlToday = "SELECT COUNT(*) FROM appointments WHERE DATE(appointment_date) = CURDATE()";
            try (PreparedStatement ps = conn.prepareStatement(sqlToday);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    todaysAppointments = rs.getInt(1);
                }
            }

            // 🔹 Total Patients
            String sqlPatients = "SELECT COUNT(*) FROM user WHERE role='patient'";
            try (PreparedStatement ps = conn.prepareStatement(sqlPatients);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    totalPatients = rs.getInt(1);
                }
            }

            // 🔹 Total Prescriptions
            String sqlPrescriptions = "SELECT COUNT(*) FROM prescriptions";
            try (PreparedStatement ps = conn.prepareStatement(sqlPrescriptions);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    totalPrescriptions = rs.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // 🔐 Escape admin name (safe JSON)
        if (adminName != null) {
            adminName = adminName.replace("\"", "\\\"");
        }

        // 🔹 Build JSON manually
        String json = "{"
                + "\"name\":\"" + adminName + "\","
                + "\"pendingDoctors\":" + pendingDoctors + ","
                + "\"todaysAppointments\":" + todaysAppointments + ","
                + "\"totalPatients\":" + totalPatients + ","
                + "\"totalPrescriptions\":" + totalPrescriptions
                + "}";

        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}