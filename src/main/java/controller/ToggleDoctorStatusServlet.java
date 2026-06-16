package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;
import org.json.JSONObject;

@WebServlet("/ToggleDoctorStatusServlet")
public class ToggleDoctorStatusServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject json = new JSONObject();

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            try (Connection conn = DBConnection.getConnection()) {

                // Check current status
                String check = "SELECT status FROM user WHERE user_id=? AND role='doctor'";
                PreparedStatement ps1 = conn.prepareStatement(check);
                ps1.setInt(1, id);
                ResultSet rs = ps1.executeQuery();

                if (rs.next()) {
                    String current = rs.getString("status");
                    String newStatus = current.equals("active") ? "inactive" : "active";

                    // Update status
                    String update = "UPDATE user SET status=? WHERE user_id=? AND role='doctor'";
                    PreparedStatement ps2 = conn.prepareStatement(update);
                    ps2.setString(1, newStatus);
                    ps2.setInt(2, id);
                    ps2.executeUpdate();

                    json.put("success", true);
                    json.put("newStatus", newStatus);

                } else {
                    json.put("success", false);
                    json.put("message", "Doctor not found");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            json.put("success", false);
            json.put("message", "Error: " + e.getMessage());
        }

        out.print(json.toString());
        out.flush();
    }
}