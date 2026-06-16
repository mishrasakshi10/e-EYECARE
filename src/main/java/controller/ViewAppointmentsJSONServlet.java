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

@WebServlet("/ViewAppointmentsJSONServlet")
public class ViewAppointmentsJSONServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {

            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                out.print("{\"error\":\"not_logged_in\"}");
                return;
            }

            int patientId = (int) session.getAttribute("userId");

            Connection con = DBConnection.getConnection();

            String sql = "SELECT u.name AS doctor_name,\r\n"
            		+ "       a.appointment_date,\r\n"
            		+ "       a.appointment_time,\r\n"
            		+ "       a.problem,\r\n"
            		+ "       a.status\r\n"
            		+ "FROM appointments a\r\n"
            		+ "JOIN user u ON a.doctor_id = u.user_id\r\n"
            		+ "WHERE a.patient_id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, patientId);

            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {

                if (!first) json.append(",");

                json.append("{")
                    .append("\"doctor\":\"").append(rs.getString("doctor_name")).append("\",")
                    .append("\"date\":\"").append(rs.getString("appointment_date")).append("\",")
                    .append("\"time\":\"").append(rs.getString("appointment_time")).append("\",")
                    .append("\"problem\":\"").append(rs.getString("problem")).append("\",")
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
