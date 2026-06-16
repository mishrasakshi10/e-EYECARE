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

@WebServlet("/LoadSlotsServlet")
public class LoadSlotsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

    	System.out.println("Doctor ID received: " + request.getParameter("doctorId"));
    	
        int doctorId = Integer.parseInt(request.getParameter("doctorId"));
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM doctor_slots WHERE doctor_id=? AND status='available'";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            StringBuilder json = new StringBuilder("[");
            while (rs.next()) {
                json.append("{")
                    .append("\"slot_id\":").append(rs.getInt("slot_id")).append(",")
                    .append("\"slot_date\":\"").append(rs.getDate("slot_date")).append("\",")
                    .append("\"slot_time\":\"").append(rs.getTime("slot_time")).append("\"")
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