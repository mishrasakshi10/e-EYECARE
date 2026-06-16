package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;
import com.eyecares.util.PasswordUtil;

@WebServlet("/ChangePasswordServlet")
public class ChangePasswordServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.getWriter().write("{\"error\":\"Session expired\"}");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");

        try (Connection conn = DBConnection.getConnection()) {

            // 1️⃣ Get stored hashed password
            String sql = "SELECT password FROM user WHERE user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String dbHashedPassword = rs.getString("password");

                // 2️⃣ Hash entered current password
                String hashedCurrent = PasswordUtil.hashPassword(currentPassword);

                if (!dbHashedPassword.equals(hashedCurrent)) {
                    response.getWriter().write("{\"error\":\"Current password is incorrect\"}");
                    return;
                }
            }

            // 3️⃣ Hash new password
            String hashedNewPassword = PasswordUtil.hashPassword(newPassword);

            // 4️⃣ Update DB
            String updateSql = "UPDATE user SET password=? WHERE user_id=?";
            ps = conn.prepareStatement(updateSql);
            ps.setString(1, hashedNewPassword);
            ps.setInt(2, userId);

            int updated = ps.executeUpdate();

            if (updated > 0) {
                response.getWriter().write("{\"success\":true}");
            } else {
                response.getWriter().write("{\"error\":\"Password update failed\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"Database error\"}");
        }
    }
}