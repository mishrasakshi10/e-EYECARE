package controller;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;

@WebServlet("/ManagePatientsServlet")
public class ManagePatientsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        // 🔐 Security check (optional but recommended)
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String search = request.getParameter("search");
        if (search == null) search = "";

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        try (Connection conn = DBConnection.getConnection()) {

            String sql =
                "SELECT user_id, name, email, age, gender, phone, status " +
                "FROM user " +
                "WHERE role='patient' AND name LIKE ?";

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
                    .append("\"age\":").append(rs.getInt("age")).append(",")
                    .append("\"gender\":\"").append(rs.getString("gender")).append("\",")
                    .append("\"phone\":\"").append(rs.getString("phone")).append("\",")
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