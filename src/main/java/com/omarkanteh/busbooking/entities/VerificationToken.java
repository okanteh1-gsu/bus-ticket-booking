package com.omarkanteh.busbooking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Data
@Table(name = "verification_confirmation")
@NoArgsConstructor
public class VerificationConfirmation {
    @Id
    private long id;

    @Column(name = "verification_token")
    private String verificationToken;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
