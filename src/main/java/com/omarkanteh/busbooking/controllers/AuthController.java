package com.omarkanteh.busbooking.controllers;

import com.omarkanteh.busbooking.dto.*;
import com.omarkanteh.busbooking.entities.ResetPassword;
import com.omarkanteh.busbooking.entities.User;
import com.omarkanteh.busbooking.mappers.UserMapper;
import com.omarkanteh.busbooking.repositories.ResetPasswordRepository;
import com.omarkanteh.busbooking.repositories.UserRepository;
import com.omarkanteh.busbooking.repositories.VerificationTokenRepository;
import com.omarkanteh.busbooking.services.CookieService;
import com.omarkanteh.busbooking.services.EmailService;
import com.omarkanteh.busbooking.services.JwtService;
import com.omarkanteh.busbooking.services.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final CookieService cookieService;
    private final UserMapper userMapper;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ResetPasswordRepository resetPasswordRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        if (!user.isVerified()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Please verify your email before logging in."));
        }

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        cookieService.setCookie(refreshToken, response);

        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@CookieValue("refreshToken") String refreshToken) {
        var jwt = jwtService.parseToken(refreshToken);
        if(jwt == null || jwt.isExpired()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var user = userRepository.findById(jwt.getUserId()).orElseThrow();
        var accessToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
    }
    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe() {
        long userId = (long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findById(userId).orElse(null);
        if(user == null) {
            return ResponseEntity.notFound().build();
        }
        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);

    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {
        try {
            var tokenEntity = verificationTokenRepository.findVerificationTokenByToken(token).orElse(null);
            if (tokenEntity == null || tokenEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Invalid or expired verification token"));
            }

            var user = tokenEntity.getUser();

            user.setVerified(true);
            userRepository.save(user);
            verificationTokenRepository.delete(tokenEntity);

            return ResponseEntity.ok(Map.of(
                    "message", "Email verified successfully",
                    "userId", user.getId()
            ));

        } catch (Exception e) {
            System.out.println("Verification failed for token: {}" + token + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Verification process failed"));
        }
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        var user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        if (user.isVerified()) {
            return ResponseEntity.ok(Map.of("message", "Email already verified"));
        }

        // regenerate and send email
        tokenService.generateVerificationToken(user);
        return ResponseEntity.ok(Map.of("message", "Verification email sent again"));
    }
    @PostMapping("/reset_password_link")
    @Transactional
    public ResponseEntity<?> resetPasswordLink(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            resetPasswordRepository.deleteByUser(user);

            String token = UUID.randomUUID().toString();
            ResetPassword resetPassword = new ResetPassword();
            resetPassword.setUser(user);
            resetPassword.setToken(token);
            resetPassword.setExpiredAt(LocalDateTime.now().plusMinutes(15));
            resetPasswordRepository.save(resetPassword);
            emailService.sendResetLink(user, token);

            try {
                emailService.sendResetLink(user, token);
            } catch (Exception e) {
                System.err.println("Failed to send reset email to " + email + ": " + e.getMessage());
            }
        }

        return ResponseEntity.ok("If an account with that email exists, a reset link has been sent.");

    }
    @Transactional
    @PostMapping("/change_password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request.getToken() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token and new password are required"));
        }

        Optional<ResetPassword> optionalResetToken = resetPasswordRepository.findByToken(request.getToken());
        if (optionalResetToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Invalid token"));
        }

        ResetPassword resetToken = optionalResetToken.get();

        if (resetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Token is expired"));
        }

        try {
            User user = resetToken.getUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "No user associated with this token"));
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.saveAndFlush(user);

            resetPasswordRepository.delete(resetToken);
            resetPasswordRepository.flush();

            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to reset password"));
        }
    }




}
