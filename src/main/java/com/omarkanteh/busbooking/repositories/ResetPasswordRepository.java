package com.omarkanteh.busbooking.repositories;

import com.omarkanteh.busbooking.entities.ResetPassword;
import com.omarkanteh.busbooking.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
    Optional<ResetPassword> findByToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM ResetPassword r WHERE r.user = :user")
    void deleteByUser(User user);
}
