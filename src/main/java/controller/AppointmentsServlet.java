package controller;

import com.eyecares.util.EmailUtil;
import com.eyecares.util.DBConnection;

import java.io.IOException;
import java.sql.*;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AppointmentsServlet")
public class AppointmentsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // -------- GET PARAMETERS --------
        String doctorParam = request.getParameter("doctorId");
        String slotParam = request.getParameter("slotId");
        String problem = request.getParameter("problem");

        if (doctorParam == null || slotParam == null ||
                doctorParam.isEmpty() || slotParam.isEmpty()) {
            response.getWriter().write("Invalid request parameters.");
            return;
        }

        int doctorId = Integer.parseInt(doctorParam);
        int slotId = Integer.parseInt(slotParam);

        // -------- SESSION CHECK --------
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int patientId = (Integer) session.getAttribute("userId");

        Connection con = null;

        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // 🔥 Transaction Start

            // -------- FETCH PATIENT DETAILS --------
            PreparedStatement patientPs = con.prepareStatement(
                    "SELECT name, email FROM user WHERE user_id=?");
            patientPs.setInt(1, patientId);
            ResultSet patientRs = patientPs.executeQuery();

            if (!patientRs.next()) {
                response.getWriter().write("Patient not found.");
                return;
            }

            String patientName = patientRs.getString("name");
            String patientEmail = patientRs.getString("email");

            // -------- CHECK SLOT AVAILABILITY --------
            PreparedStatement slotPs = con.prepareStatement(
                    "SELECT slot_date, slot_time FROM doctor_slots WHERE slot_id=? AND status='available'");
            slotPs.setInt(1, slotId);
            ResultSet slotRs = slotPs.executeQuery();

            if (!slotRs.next()) {
                response.getWriter().write("Selected slot is no longer available.");
                return;
            }

            Date appointmentDate = slotRs.getDate("slot_date");
            Time appointmentTime = slotRs.getTime("slot_time");

            // -------- INSERT APPOINTMENT --------
            PreparedStatement insertPs = con.prepareStatement(
                    "INSERT INTO appointments "
                            + "(patient_id, doctor_id, appointment_date, appointment_time, problem, status, slot_id) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)");

            insertPs.setInt(1, patientId);
            insertPs.setInt(2, doctorId);
            insertPs.setDate(3, appointmentDate);
            insertPs.setTime(4, appointmentTime);
            insertPs.setString(5, problem);
            insertPs.setString(6, "pending");  // ✅ FIXED HERE
            insertPs.setInt(7, slotId);

            insertPs.executeUpdate();

            // -------- UPDATE SLOT STATUS --------
            PreparedStatement updateSlotPs = con.prepareStatement(
                    "UPDATE doctor_slots SET status='booked' WHERE slot_id=?");
            updateSlotPs.setInt(1, slotId);
            updateSlotPs.executeUpdate();

            con.commit();  // 🔥 Transaction Commit

            // -------- SEND CONFIRMATION EMAIL --------
            String subject = "Appointment Booking Confirmation - e-EYECARE";
            String body = "Dear " + patientName + ",\n\n"
                    + "Your appointment has been successfully booked.\n\n"
                    + "Appointment Date: " + appointmentDate + "\n"
                    + "Appointment Time: " + appointmentTime + "\n"
                    + "Status: PENDING (Waiting for Doctor Approval)\n\n"
                    + "You will receive another email once the doctor approves or rejects.\n\n"
                    + "Thank you for choosing e-EYECARE.\n\n"
                    + "Regards,\n"
                    + "e-EYECARE Team";

            try {
                EmailUtil.sendEmail(patientEmail, subject, body);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            response.sendRedirect("patient/dashboard.html?booking=success");

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback(); // 🔥 Rollback if error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            response.getWriter().write("Error booking appointment.");
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}