package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;

@WebServlet("/DoctorProfileServlet")
public class DoctorProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null
                || !"doctor".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        int doctorId = (int) session.getAttribute("userId");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (Connection conn = DBConnection.getConnection()) {

            // Join user and doctor_details
            String sql = "SELECT u.name, u.email, u.phone, "
                    + "d.qualification, d.specialization, d.experience, "
                    + "d.registration_no, d.consultation_fee, d.profile_photo "
                    + "FROM user u "
                    + "LEFT JOIN doctor_details d ON u.user_id = d.doctor_id "
                    + "WHERE u.user_id = ? AND u.role = 'doctor'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // Handle nulls in doctor_details
                String qualification = rs.getString("qualification") != null ? rs.getString("qualification") : "";
                String specialization = rs.getString("specialization") != null ? rs.getString("specialization") : "";
                int experience = rs.getInt("experience"); // 0 if null
                String registrationNo = rs.getString("registration_no") != null ? rs.getString("registration_no") : "";
                double consultationFee = rs.getDouble("consultation_fee"); // 0.0 if null
                String profilePhoto = rs.getString("profile_photo");

                if (profilePhoto == null || profilePhoto.trim().isEmpty()) {
                    profilePhoto = "doctor-image.jpg";
                }

                // Build JSON manually
                String json = "{"
                        + "\"name\":\"" + rs.getString("name") + "\","
                        + "\"email\":\"" + rs.getString("email") + "\","
                        + "\"phone\":\"" + rs.getString("phone") + "\","
                        + "\"qualification\":\"" + qualification + "\","
                        + "\"specialization\":\"" + specialization + "\","
                        + "\"experience\":" + experience + ","
                        + "\"registrationNo\":\"" + registrationNo + "\","
                        + "\"consultationFee\":" + consultationFee + ","
                        + "\"profilePhoto\":\"" + profilePhoto + "\""
                        + "}";

                response.getWriter().write(json);

            } else {
                response.getWriter().write("{\"error\":\"Doctor not found\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"Database error\"}");
        }
    }
}