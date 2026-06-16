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

@WebServlet("/AdminProfileServlet")
public class AdminProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);

        // ===== Session Check =====
        if (session == null || session.getAttribute("user_id") == null) {
            out.print("{\"error\":\"Session expired. Please login again.\"}");
            return;
        }

        // ===== Safe user_id fetch =====
        Object idObj = session.getAttribute("user_id");
        int userId = Integer.parseInt(idObj.toString());

        System.out.println("AdminProfileServlet user_id: " + userId);

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT name, email, phone FROM user WHERE user_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                // Debug print
                System.out.println("Name from DB: " + name);
                System.out.println("Email from DB: " + email);
                System.out.println("Phone from DB: " + phone);

                // Null safety
                if (name == null) name = "";
                if (email == null) email = "";
                if (phone == null) phone = "";

                // Proper JSON response
                String json = "{"
                        + "\"name\":\"" + name + "\","
                        + "\"email\":\"" + email + "\","
                        + "\"phone\":\"" + phone + "\""
                        + "}";

                out.print(json);

            } else {
                out.print("{\"error\":\"Admin not found\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"Server error\"}");
        }
    }
}