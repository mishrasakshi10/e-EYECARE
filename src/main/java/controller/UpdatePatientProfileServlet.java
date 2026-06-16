package controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import javax.servlet.http.Part;

import com.eyecares.util.DBConnection;

@WebServlet("/UpdatePatientProfileServlet")
@MultipartConfig
public class UpdatePatientProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.getWriter().write("Session expired");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        // 🔥 File upload
        Part filePart = request.getPart("profilePhoto");
        String fileName = null;

        if (filePart != null && filePart.getSize() > 0) {
            fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();

            String uploadPath = getServletContext().getRealPath("") + File.separator + "images";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();

            filePart.write(uploadPath + File.separator + fileName);
        }

        try (Connection conn = DBConnection.getConnection()) {

            PreparedStatement ps;

            if (fileName != null) {
                ps = conn.prepareStatement(
                    "UPDATE user SET name=?, email=?, phone=?, profile_photo=? WHERE user_id=?"
                );

                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setString(4, fileName);
                ps.setInt(5, userId);

            } else {
                ps = conn.prepareStatement(
                    "UPDATE user SET name=?, email=?, phone=? WHERE user_id=?"
                );

                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setInt(4, userId);
            }

            ps.executeUpdate();
            ps.close();

            response.getWriter().write("success");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("error");
        }
    }
}