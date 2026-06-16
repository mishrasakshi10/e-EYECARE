package com.eyecares.util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {

    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        // Check if email is null or empty
        if (toEmail == null || toEmail.trim().isEmpty()) {
            System.out.println("EmailUtil: Cannot send email. Recipient email is null or empty.");
            return; // skip sending
        }

        final String fromEmail = "mishrasakshi61049@gmail.com"; // your Gmail
        final String password = "cqhs wgbf chpb idvi";           // App Password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
        System.out.println("Email sent successfully to " + toEmail);
    }

    // Optional test
    public static void main(String[] args) throws MessagingException {
        sendEmail("khushipal04806@gmail.com", "Test Email", "This is a test email from JavaMail.");
    }
}
