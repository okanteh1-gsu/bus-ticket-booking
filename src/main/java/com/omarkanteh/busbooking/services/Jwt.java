package com.omarkanteh.busbooking.services;

import com.omarkanteh.busbooking.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;


@AllArgsConstructor
public class Jwt {
    private  final Claims claims;
    private final SecretKey secretKey;
    
    public boolean isExpired(){
        return claims.getExpiration().before(new Date());
    }
    
    public Long getUserId(){
        return  Long.valueOf(claims.getSubject());
    }
    
    public UserRole getRole(){
        return UserRole.valueOf(claims.get("role", String.class));
    }

    @Override
    public String toString() {
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }
}
