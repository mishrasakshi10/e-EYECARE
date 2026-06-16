package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.eyecares.util.DBConnection;

@WebServlet("/ManageDoctorsServlet")
public class ManageDoctorsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String search = request.getParameter("search");
        if (search == null) search = "";

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (Connection conn = DBConnection.getConnection()) {

            String sql =
                "SELECT u.user_id, u.name, u.email, u.status, " +
                "d.specialization, d.experience, d.consultation_fee " +
                "FROM user u " +
                "JOIN doctor_details d ON u.user_id = d.doctor_id " +
                "WHERE u.role='doctor' AND u.status != 'pending' " +
                "AND u.name LIKE ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + search + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"user_id\":").append(rs.getInt("user_id")).append(",")
                    .append("\"name\":\"").append(rs.getString("name")).append("\",")
                    .append("\"email\":\"").append(rs.getString("email")).append("\",")
                    .append("\"specialization\":\"").append(rs.getString("specialization")).append("\",")
                    .append("\"experience\":").append(rs.getInt("experience")).append(",")
                    .append("\"fees\":").append(rs.getDouble("consultation_fee")).append(",")
                    .append("\"status\":\"").append(rs.getString("status")).append("\"")
                    .append("}");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        json.append("]");
        response.getWriter().print(json.toString());
    }
}