package com.omarkanteh.busbooking.repositories;

import com.omarkanteh.busbooking.entities.VerificationConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationConfirmationRepository extends JpaRepository<VerificationConfirmation, Long> {
    Optional<VerificationConfirmation> findByVerificationToken(String verificationToken);
}
