package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;

@WebServlet("/addSlots")
public class addSlots extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String date = request.getParameter("slotDate");
        String time = request.getParameter("slotTime");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.getWriter().write("Session expired");
            return;
        }

        int doctorId = (int) session.getAttribute("userId");

        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO doctor_slots (doctor_id, slot_date, slot_time, status) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            ps.setString(4, "available");

            ps.executeUpdate();

            response.getWriter().write("success");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}