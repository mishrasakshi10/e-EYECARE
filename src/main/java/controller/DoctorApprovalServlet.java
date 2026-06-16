package controller;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.eyecares.util.DBConnection;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/DoctorApprovalServlet")
public class DoctorApprovalServlet extends HttpServlet {

    // GET = fetch pending doctors
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        JSONArray doctors = new JSONArray();

        String sql = "SELECT u.user_id, u.name, u.email, u.phone, u.age, u.gender, u.status, "
                   + "d.qualification, d.specialization, d.experience, d.registration_no, d.consultation_fee, d.profile_photo AS doctor_photo "
                   + "FROM user u "
                   + "LEFT JOIN doctor_details d ON u.user_id = d.doctor_id "
                   + "WHERE u.role='doctor' AND u.status='pending'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                JSONObject doc = new JSONObject();

                doc.put("user_id", rs.getInt("user_id"));
                doc.put("name", rs.getString("name"));
                doc.put("email", rs.getString("email"));
                doc.put("phone", rs.getString("phone"));
                doc.put("age", rs.getInt("age"));
                doc.put("gender", rs.getString("gender"));
                doc.put("status", rs.getString("status"));

                // Doctor details with defaults
                String qualification = rs.getString("qualification");
                doc.put("qualification", qualification != null ? qualification : "Not filled");

                String specialization = rs.getString("specialization");
                doc.put("specialization", specialization != null ? specialization : "Not filled");

                int experience = rs.getInt("experience");
                if(rs.wasNull()) experience = 0;
                doc.put("experience", experience);

                String registrationNo = rs.getString("registration_no");
                doc.put("registration_no", registrationNo != null ? registrationNo : "Not filled");

                double consultationFee = rs.getDouble("consultation_fee");
                if(rs.wasNull()) consultationFee = 0.0;
                doc.put("consultation_fee", consultationFee);

                String doctorPhoto = rs.getString("doctor_photo");
                doc.put("doctor_photo", doctorPhoto != null ? doctorPhoto : "default.jpg");

                doctors.put(doc);
            }

            response.getWriter().print(doctors.toString());

        } catch(Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    // POST = handle approve/reject
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = Integer.parseInt(request.getParameter("user_id"));
        String status = request.getParameter("status"); // "approved" or "rejected"

        // Map status to correct value in DB
        String newStatus = "active"; // approved
        if("rejected".equalsIgnoreCase(status)) newStatus = "rejected";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE user SET status=? WHERE user_id=?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, userId);

            int i = ps.executeUpdate();
            if(i > 0) {
                response.getWriter().println("Doctor " + (newStatus.equals("active") ? "approved" : "rejected") + " successfully!");
            } else {
                response.getWriter().println("Failed to update status.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}