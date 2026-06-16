package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.eyecares.util.DBConnection;

@WebServlet("/ManageAppointmentsServlet")
public class ManageAppointmentsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray jsonArray = new JSONArray();

        String search = request.getParameter("search");
        if (search == null) search = "";

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT a.id, a.doctor_name, a.appointment_date, " +
                         "a.appointment_time, a.problem, a.status, " +
                         "u.name AS patient_name " +
                         "FROM appointments a " +
                         "JOIN `user` u ON a.patient_id = u.user_id " +
                         "WHERE u.name LIKE ? OR a.doctor_name LIKE ? " +
                         "ORDER BY a.appointment_date DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + search + "%");
            ps.setString(2, "%" + search + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                JSONObject obj = new JSONObject();

                obj.put("id", rs.getInt("id"));
                obj.put("patient_name", rs.getString("patient_name"));
                obj.put("doctor_name", rs.getString("doctor_name"));
                obj.put("appointment_date", rs.getDate("appointment_date"));
                obj.put("appointment_time", rs.getString("appointment_time"));
                obj.put("problem", rs.getString("problem"));
                obj.put("status", rs.getString("status"));

                jsonArray.put(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().print(jsonArray.toString());
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String id = request.getParameter("id");

        try (Connection con = DBConnection.getConnection()) {

            if ("update".equals(action)) {

                String status = request.getParameter("status");

                String sql = "UPDATE appointments SET status=? WHERE id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, status);
                ps.setInt(2, Integer.parseInt(id));

                int rows = ps.executeUpdate();

                if (rows > 0)
                    response.getWriter().write("Status Updated Successfully");
                else
                    response.getWriter().write("Update Failed");

            } else if ("delete".equals(action)) {

                String sql = "DELETE FROM appointments WHERE id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(id));

                int rows = ps.executeUpdate();

                if (rows > 0)
                    response.getWriter().write("Deleted Successfully");
                else
                    response.getWriter().write("Delete Failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Error occurred");
        }
    }
    
}