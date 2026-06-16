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

@WebServlet("/ViewPrescriptionsServlet")
public class ViewPrescriptionsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);

        // 🔐 1. Check login
        if (session == null || session.getAttribute("role") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Please login first\"}");
            return;
        }

        String role = (String) session.getAttribute("role");

        // 🔐 2. Allow only Patient
        if (!role.equalsIgnoreCase("patient")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"error\":\"Access denied\"}");
            return;
        }

        int patientId = (int) session.getAttribute("userId");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();

            String sql = "SELECT p.prescription_id, u.name AS doctor_name, "
                    + "p.diagnosis, p.medicines, p.notes, p.created_at "
                    + "FROM prescriptions p "
                    + "JOIN user u ON p.doctor_id = u.user_id "
                    + "WHERE p.patient_id = ? "
                    + "ORDER BY p.created_at DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, patientId);

            rs = ps.executeQuery();

            StringBuilder json = new StringBuilder();
            json.append("[");

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                json.append("{");
                json.append("\"prescription_id\":").append(rs.getInt("prescription_id")).append(",");
                json.append("\"doctor\":\"").append(rs.getString("doctor_name")).append("\",");
                json.append("\"diagnosis\":\"").append(rs.getString("diagnosis")).append("\",");
                json.append("\"medicines\":\"").append(rs.getString("medicines")).append("\",");
                json.append("\"notes\":\"").append(rs.getString("notes")).append("\",");
                json.append("\"date\":\"").append(rs.getTimestamp("created_at")).append("\"");
                json.append("}");

                first = false;
            }

            json.append("]");

            out.print(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Server Error\"}");
        }
    }
}
