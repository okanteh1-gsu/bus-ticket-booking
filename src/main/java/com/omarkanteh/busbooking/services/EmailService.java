package com.omarkanteh.busbooking.services;


import com.omarkanteh.busbooking.config.MailConfig;
import com.omarkanteh.busbooking.entities.Booking;
import com.omarkanteh.busbooking.entities.Seat;
import com.omarkanteh.busbooking.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final MailConfig mailConfig;

    @Async
    public void sendConfirmationEmail(User user, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailConfig.getUsername());
        message.setTo(user.getEmail());
        message.setSubject("Complete your registration");
        String emailText =
                "Hi " + user.getFirstName() + ",\n\n" +
                        "Thank you for registering with us!\n\n" +
                        "To complete your registration, please click the link below to verify your email address:\n\n" +
                        "Verify your email: http://localhost:8080/auth/verify-email?token=" + token + "\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "If you didn't create an account with us, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "The Support Team";
        message.setText(emailText);
        javaMailSender.send(message);
    }
    @Async
    public void sendBookingConfirmation(String usersEmail, Booking booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailConfig.getUsername());
        message.setTo(usersEmail);
        message.setSubject("Booking Confirmation - Bus Ticket");
        message.setText("Hi " + booking.getUser().getFirstName() + ",\n\n"
                + "Your booking is confirmed.\n"
                + "Schedule: " + booking.getSchedule().getRoute().getDestination() + "\n"
                + "Date: " + booking.getSchedule().getDepartureTime() + "\n"
                + "Seats: " + booking.getSeats().stream().map(Seat::getSeatNumber)
                .collect(Collectors.joining(", ")) + "\n"
                + "Total Fare: $" + booking.getTotalFare() + "\n\n"
                + "Thank you for booking with us.");
        javaMailSender.send(message);
    }
    @Async
    public void sendResetLink(User user, String token) {
        String resetUrl = "http://localhost:8080/auth/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailConfig.getUsername());
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        message.setText("Hello " + user.getFirstName() + ",\n\n"
                + "You requested to reset your password. Click the link below to reset it:\n"
                + resetUrl + "\n\n"
                + "This link will expire in 30 minutes.\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Regards,\n"
                + "Your App Team");

        javaMailSender.send(message);
    }
}
