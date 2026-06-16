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

@WebServlet("/UpdateDoctorProfileServlet")
@MultipartConfig
public class UpdateDoctorProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");

        HttpSession session = request.getSession(false);
        if (session == null || !"doctor".equals(session.getAttribute("role"))) {
            response.getWriter().write("Unauthorized");
            return;
        }

        int userId = (int) session.getAttribute("userId");

        // 🔹 Get form data
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String qualification = request.getParameter("qualification");
        String specialization = request.getParameter("specialization");
        int experience = Integer.parseInt(request.getParameter("experience"));
        String registrationNo = request.getParameter("registrationNo");
        double consultationFee = Double.parseDouble(request.getParameter("consultationFee"));

        // 🔹 File upload
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

            // 🔹 Update user table
            PreparedStatement ps1 = conn.prepareStatement(
                "UPDATE user SET name=?, email=?, phone=? WHERE user_id=?"
            );
            ps1.setString(1, fullName);
            ps1.setString(2, email);
            ps1.setString(3, phone);
            ps1.setInt(4, userId);
            ps1.executeUpdate();
            ps1.close();

            // 🔹 Update doctor_details table
            PreparedStatement ps2;

            if (fileName != null) {
                ps2 = conn.prepareStatement(
                    "UPDATE doctor_details SET qualification=?, specialization=?, experience=?, registration_no=?, consultation_fee=?, profile_photo=? WHERE doctor_id=?"
                );

                ps2.setString(1, qualification);
                ps2.setString(2, specialization);
                ps2.setInt(3, experience);
                ps2.setString(4, registrationNo);
                ps2.setDouble(5, consultationFee);
                ps2.setString(6, fileName);
                ps2.setInt(7, userId);

            } else {
                ps2 = conn.prepareStatement(
                    "UPDATE doctor_details SET qualification=?, specialization=?, experience=?, registration_no=?, consultation_fee=? WHERE doctor_id=?"
                );

                ps2.setString(1, qualification);
                ps2.setString(2, specialization);
                ps2.setInt(3, experience);
                ps2.setString(4, registrationNo);
                ps2.setDouble(5, consultationFee);
                ps2.setInt(6, userId);
            }

            ps2.executeUpdate();
            ps2.close();

            response.getWriter().write("success");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("error");
        }
    }
}