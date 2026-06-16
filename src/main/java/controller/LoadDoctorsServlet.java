package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eyecares.util.DBConnection;

@WebServlet("/LoadDoctorsServlet")
public class LoadDoctorsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT u.user_id, u.name, d.specialization " +
                         "FROM user u JOIN doctor_details d " +
                         "ON u.user_id = d.doctor_id " +
                         "WHERE u.role='doctor' AND u.status='active'";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                json.append("{")
                    .append("\"doctor_id\":").append(rs.getInt("user_id")).append(",")
                    .append("\"name\":\"").append(rs.getString("name")).append("\",")
                    .append("\"specialization\":\"").append(rs.getString("specialization")).append("\"")
                    .append("},");
            }

            if(json.length() > 1)
                json.deleteCharAt(json.length()-1);

            json.append("]");
            out.print(json);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}