package controller;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.eyecares.util.DBConnection;

@WebServlet("/ManagePrescriptionsServlet")
public class ManagePrescriptionsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray array = new JSONArray();
        String search = request.getParameter("search");
        if (search == null) search = "";

        try (Connection con = DBConnection.getConnection()) {

            String sql =
                "SELECT p.*, " +
                "pat.name AS patient_name, " +
                "doc.name AS doctor_name " +
                "FROM prescriptions p " +
                "JOIN user pat ON p.patient_id = pat.user_id " +
                "JOIN user doc ON p.doctor_id = doc.user_id " +
                "WHERE pat.name LIKE ? OR doc.name LIKE ? " +
                "ORDER BY p.created_at DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + search + "%");
            ps.setString(2, "%" + search + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                JSONObject obj = new JSONObject();

                obj.put("prescription_id",
                        rs.getInt("prescription_id"));
                obj.put("patient_name",
                        rs.getString("patient_name"));
                obj.put("doctor_name",
                        rs.getString("doctor_name"));
                obj.put("diagnosis",
                        rs.getString("diagnosis"));
                obj.put("medicines",
                        rs.getString("medicines"));
                obj.put("notes",
                        rs.getString("notes"));
                obj.put("created_at",
                        rs.getTimestamp("created_at"));

                array.put(obj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().print(array.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("delete".equals(action)) {

            int id = Integer.parseInt(
                request.getParameter("prescription_id"));

            try (Connection con = DBConnection.getConnection()) {

                PreparedStatement ps =
                    con.prepareStatement(
                        "DELETE FROM prescriptions WHERE prescription_id=?");

                ps.setInt(1, id);
                ps.executeUpdate();

                response.getWriter()
                        .print("Prescription Deleted Successfully");

            } catch (Exception e) {
                e.printStackTrace();
                response.getWriter()
                        .print("Error deleting prescription");
            }
        }
    }
}