package com.omarkanteh.busbooking.entities;

import com.omarkanteh.busbooking.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "First name is required")
        @Column(name = "first_name")
        private String firstName;

        @NotBlank(message = "Last name is required")
        @Column(name = "last_name")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Column(unique = true)
        private String email;


        @NotBlank(message = "Password is required")
        private String password;

        @Column(name = "phone_number")
        private String phoneNumber;

        @Enumerated(EnumType.STRING)
        @Column(name = "user_role")
        private UserRole role; // ADMIN, CUSTOMER, BUS_OPERATOR

        @OneToMany(mappedBy = "user")
        private List<Booking> bookings;

        @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
        private VerificationToken verificationConfirmation;

        @OneToOne(mappedBy = "user")
        private ResetPassword resetPassword;

        @Column(name = "is_verified")
        private boolean isVerified;
}

