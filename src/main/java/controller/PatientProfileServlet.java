package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;

@WebServlet("/PatientProfileServlet")
public class PatientProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.print("{\"error\":\"User not logged in\"}");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            // 1️⃣ Fetch user details INCLUDING profile photo
            String sqlUser = "SELECT name, email, phone, profile_photo FROM user WHERE user_id = ?";
            pst = conn.prepareStatement(sqlUser);
            pst.setInt(1, userId);
            rs = pst.executeQuery();

            String name = "N/A";
            String email = "N/A";
            String phone = "N/A";
            String profilePhoto = "";

            if (rs.next()) {
                name = rs.getString("name");
                email = rs.getString("email");
                phone = rs.getString("phone");
                profilePhoto = rs.getString("profile_photo");

                if (profilePhoto == null || profilePhoto.trim().isEmpty()) {
                    profilePhoto = "user-avatar.png";
                }
            }

            rs.close();
            pst.close();

            // 2️⃣ Count upcoming appointments
            String sqlAppointments = 
                "SELECT COUNT(*) AS total FROM appointments WHERE patient_id = ? AND appointment_date >= CURDATE()";
            pst = conn.prepareStatement(sqlAppointments);
            pst.setInt(1, userId);
            rs = pst.executeQuery();

            int upcomingAppointments = 0;
            if (rs.next()) {
                upcomingAppointments = rs.getInt("total");
            }

            // 3️⃣ Build JSON INCLUDING profilePhoto
            String json = "{"
                    + "\"name\":\"" + name + "\","
                    + "\"email\":\"" + email + "\","
                    + "\"phone\":\"" + phone + "\","
                    + "\"profilePhoto\":\"" + profilePhoto + "\","
                    + "\"upcomingAppointments\":" + upcomingAppointments
                    + "}";

            out.print(json);

        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"error\":\"Database error\"}");
        } finally {
            try { if (rs != null) rs.close(); } catch(Exception e) {}
            try { if (pst != null) pst.close(); } catch(Exception e) {}
            try { if (conn != null) conn.close(); } catch(Exception e) {}
        }
    }
}