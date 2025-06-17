package com.omarkanteh.busbooking.services;

import com.omarkanteh.busbooking.entities.User;
import com.omarkanteh.busbooking.entities.VerificationToken;
import com.omarkanteh.busbooking.repositories.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    public VerificationToken generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        System.out.println("Bearer " + token);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiredAt(LocalDateTime.now().plusHours(24));
        verificationToken.setUser(user);
        return verificationTokenRepository.save(verificationToken);
    }
}
