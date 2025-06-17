package com.omarkanteh.busbooking.services;

import com.omarkanteh.busbooking.config.JwtConfig;
import com.omarkanteh.busbooking.config.MailConfig;
import com.omarkanteh.busbooking.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@AllArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;
    private final MailConfig mailConfig;

    public Jwt generateAccessToken(User user) {
        return getJwtToken(user, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(User user) {
        return getJwtToken(user, jwtConfig.getRefreshTokenExpiration());
    }
    public Jwt generateVerificationToken(User user) {
        return getJwtToken(user, mailConfig.getVerificationTokenExpiration() );
    }

    private Jwt getJwtToken(User user, long tokenExpiration) {
       Claims claims = Jwts.claims()
                .subject(user.getId().toString())
                .add("email", user.getEmail())
                .add("name", user.getFirstName())
                .add("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .build();
        return new Jwt(claims, jwtConfig.getSecretKey());
    }
    public Jwt parseToken(String token) {
        try {
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (JwtException e) {
            return null;
        }
    }


    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
