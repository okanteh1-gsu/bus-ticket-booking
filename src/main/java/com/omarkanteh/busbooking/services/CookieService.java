package com.omarkanteh.busbooking.services;

import com.omarkanteh.busbooking.config.JwtConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CookieService {
    private final JwtConfig jwtConfig;
    public void setCookie(Jwt refreshToken, HttpServletResponse response) {
        var cookie = new Cookie("refreshToken", refreshToken.toString());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge((int) jwtConfig.getRefreshTokenExpiration());
        response.addCookie(cookie);
    }
}
