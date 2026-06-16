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

@WebServlet("/ViewDoctorAppointmentsServlet")
public class ViewDoctorAppointmentsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null ||
                !"doctor".equals(session.getAttribute("role"))) {
                out.print("{\"error\":\"not_logged_in\"}");
                return;
            }

            int doctorId = (Integer) session.getAttribute("userId");

            Connection con = DBConnection.getConnection();
            String sql = "SELECT a.id, u.name AS patient_name, " +
                         "a.appointment_date, a.appointment_time, a.problem, a.status " +
                         "FROM appointments a " +
                         "JOIN user u ON a.patient_id = u.user_id " +
                         "WHERE a.doctor_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {
                if (!first) json.append(",");
                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"patient\":\"").append(rs.getString("patient_name").replace("\"","\\\"")).append("\",")
                    .append("\"date\":\"").append(rs.getString("appointment_date")).append("\",")
                    .append("\"time\":\"").append(rs.getString("appointment_time")).append("\",")
                    .append("\"problem\":\"").append(rs.getString("problem").replace("\"","\\\"")).append("\",")
                    .append("\"status\":\"").append(rs.getString("status")).append("\"")
                    .append("}");
                first = false;
            }

            json.append("]");
            out.print(json.toString());

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"server_error\"}");
        }
    }
}
