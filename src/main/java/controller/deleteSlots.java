package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;

@WebServlet("/deleteSlots")
public class deleteSlots extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String id = request.getParameter("id");

        try (Connection con = DBConnection.getConnection()) {

            String sql = "DELETE FROM doctor_slots WHERE slot_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(id));

            ps.executeUpdate();

            response.getWriter().write("deleted");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}