package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;

@WebServlet("/viewSlots")
public class viewSlots extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            out.print("[]");
            return;
        }

        int doctorId = (int) session.getAttribute("userId");

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM doctor_slots WHERE doctor_id=? ORDER BY slot_date, slot_time";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, doctorId);

            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");

                json.append("{")
                    .append("\"id\":").append(rs.getInt("slot_id")).append(",")
                    .append("\"date\":\"").append(rs.getDate("slot_date")).append("\",")
                    .append("\"time\":\"").append(rs.getTime("slot_time")).append("\",")
                    .append("\"status\":\"").append(rs.getString("status")).append("\"")
                    .append("}");

                first = false;
            }

            json.append("]");
            out.print(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}