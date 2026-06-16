package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.sql.Time;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.eyecares.util.DBConnection;
import com.eyecares.util.EmailUtil;

@WebServlet("/UpdateAppointmentsStatusServlet")
public class UpdateAppointmentsStatusServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null ||
            !"doctor".equals(session.getAttribute("role"))) {
            response.getWriter().print("Not authorized");
            return;
        }

        int doctorId = (Integer) session.getAttribute("userId");
        int appointmentId = Integer.parseInt(request.getParameter("id"));
        String status = request.getParameter("status"); // approved or rejected

        try (Connection con = DBConnection.getConnection()) {
            // 1️⃣ Update appointment status
            String sql = "UPDATE appointments SET status=? WHERE id=? AND doctor_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, appointmentId);
            ps.setInt(3, doctorId);

            int updated = ps.executeUpdate();

            if (updated > 0) {
                // 2️⃣ Fetch patient and doctor info
                String infoSql = "SELECT p.name AS patient_name, p.email AS patient_email, " +
                                 "a.appointment_date, a.appointment_time, d.name AS doctor_name " +
                                 "FROM appointments a " +
                                 "JOIN user p ON a.patient_id = p.user_id " +
                                 "JOIN user d ON a.doctor_id = d.user_id " +
                                 "WHERE a.id=?";
                PreparedStatement ps2 = con.prepareStatement(infoSql);
                ps2.setInt(1, appointmentId);
                ResultSet rs = ps2.executeQuery();

                if (rs.next()) {
                    String patientName = rs.getString("patient_name");
                    String patientEmail = rs.getString("patient_email");
                    String doctorName = rs.getString("doctor_name"); // ✅ doctor name
                    Date date = rs.getDate("appointment_date");
                    Time time = rs.getTime("appointment_time");

                    // 3️⃣ Prepare and send email
                    String subject = "Your Appointment has been " + status.toUpperCase();
                    String body = "Dear " + patientName + ",\n\n" +
                            "Your appointment with Dr. " + doctorName +
                            " on " + date + " at " + time +
                            " has been " + status + " by your doctor.\n\n" +
                            "Regards,\nE-EYECARE Team";

                    try {
                        EmailUtil.sendEmail(patientEmail, subject, body);
                    } catch (Exception e) {
                        e.printStackTrace(); // Email failed but status is updated
                    }
                }

                rs.close();
                ps2.close();

                response.getWriter().print("Status updated and email sent successfully");
            } else {
                response.getWriter().print("Update failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print("Server error");
        }
    }
}