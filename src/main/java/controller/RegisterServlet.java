package controller;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Read form values
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String phone = request.getParameter("phone");
            String role = request.getParameter("role");
            String ageStr = request.getParameter("age");
            String gender = request.getParameter("gender");

            int age = (ageStr != null && !ageStr.isEmpty()) ? Integer.parseInt(ageStr) : 0;

            // 2. Validate role to prevent admin registration
            if(role == null || (!role.equalsIgnoreCase("patient") && !role.equalsIgnoreCase("doctor"))) {
                response.getWriter().println("❌ Invalid role selected.");
                return;
            }

            // 3. Set status
            String status = role.equalsIgnoreCase("doctor") ? "pending" : "active";

            String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 4. Connect to DB
            Connection con = DBConnection.getConnection();

            // Insert into user table
            String sqlUser = "INSERT INTO user(name, email, password, age, role, gender, phone, status, created_at) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement psUser = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, name);
            psUser.setString(2, email);
            psUser.setString(3, hashPassword(password));
            psUser.setInt(4, age);
            psUser.setString(5, role);
            psUser.setString(6, gender);
            psUser.setString(7, phone);
            psUser.setString(8, status);
            psUser.setString(9, createdAt);

            int i = psUser.executeUpdate();

            if (i > 0) {
                System.out.println("✅ Registration successful for " + email);

                // If doctor, insert default row into doctor_details
                if(role.equalsIgnoreCase("doctor")) {
                    // Get generated user_id
                    ResultSet rs = psUser.getGeneratedKeys();
                    int userId = 0;
                    if(rs.next()) userId = rs.getInt(1);
                    rs.close();

                    // Insert default doctor_details
                    String sqlDoctor = "INSERT INTO doctor_details(doctor_id, qualification, specialization, experience, registration_no, consultation_fee, profile_photo) VALUES(?,?,?,?,?,?,?)";
                    PreparedStatement psDoctor = con.prepareStatement(sqlDoctor);
                    psDoctor.setInt(1, userId);
                    psDoctor.setString(2, "Not specified");
                    psDoctor.setString(3, "Not specified");
                    psDoctor.setInt(4, 0);
                    psDoctor.setString(5, "TBD");
                    psDoctor.setDouble(6, 0);
                    psDoctor.setString(7, "default.png");
                    psDoctor.executeUpdate();
                    psDoctor.close();

                    // Redirect doctor
                    response.sendRedirect("login.html?success=pending_approval");
                } else {
                    // Redirect patient
                    response.sendRedirect("login.html?success=registered");
                }

            } else {
                System.out.println("❌ Registration failed for " + email);
                response.getWriter().println("Registration failed. Try again!");
            }

            psUser.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    // SHA-256 hash method
    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}