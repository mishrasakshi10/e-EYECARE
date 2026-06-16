package controller;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.eyecares.util.DBConnection;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("Login attempt -> Username: " + username);

        try {
            // -------- VALIDATION --------
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                response.sendRedirect("login.html?error=empty");
                return;
            }

            // -------- CLEAN INPUT --------
            username = username.trim().replaceAll("\\s+", "");
            String hashedPassword = hashPassword(password);

            System.out.println("Clean Username: [" + username + "]");
            System.out.println("Hashed Password: " + hashedPassword);

            // -------- DATABASE LOGIN LOGIC --------
            Connection con = DBConnection.getConnection();
            String sql = "SELECT user_id, name, role, email, status FROM user "
                       + "WHERE (REPLACE(phone,' ','') = ? OR email = ?) "
                       + "AND password = ? LIMIT 1";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setString(3, hashedPassword);

            System.out.println("Executing Login Query...");
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Login SUCCESS for: " + username);

                // -------- FETCH ROLE & STATUS --------
                String role = rs.getString("role").trim().toLowerCase();
                String status = rs.getString("status").trim().toLowerCase();
                System.out.println("Role fetched from DB: [" + role + "]");
                System.out.println("Status fetched from DB: [" + status + "]");

                // -------- DOCTOR PENDING APPROVAL CHECK --------
                if (role.equals("doctor") && status.equals("pending")) {
                    System.out.println("Doctor login blocked: pending approval");
                    response.sendRedirect("login.html?error=pendingApproval");
                    return;
                }

                // -------- INACTIVE STATUS CHECK --------
                if (!status.equals("active")) {
                    System.out.println("Login blocked: inactive user");
                    response.sendRedirect("login.html?error=inactive");
                    return;
                }

                // -------- SESSION SETUP --------
                HttpSession session = request.getSession();
                session.setAttribute("userId", rs.getInt("user_id"));
                session.setAttribute("userName", rs.getString("name"));
                session.setAttribute("role", role);
                session.setAttribute("email", rs.getString("email"));

                System.out.println("Session created for user ID: " + rs.getInt("user_id"));

                // -------- ROLE-BASED REDIRECT --------
                switch (role) {
                    case "patient":
                        response.sendRedirect(request.getContextPath() + "/patient/dashboard.html");
                        break;
                    case "doctor":
                        response.sendRedirect(request.getContextPath() + "/doctor/dashboard.html");
                        break;
                    case "admin":
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard.html");
                        break;
                    default:
                        System.out.println("Invalid role: " + role);
                        response.sendRedirect(request.getContextPath() + "/login.html?error=invalidRole");
                        break;
                }

            } else {
                System.out.println("Login failed: invalid credentials -> " + username);
                response.sendRedirect("login.html?error=invalid");
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.html?error=exception");
        }
    }

    // SHA-256 password hashing
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
