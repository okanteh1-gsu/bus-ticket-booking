package com.omarkanteh.busbooking.controllers;

import com.omarkanteh.busbooking.dto.ChangePasswordRequest;
import com.omarkanteh.busbooking.dto.ErrorResponse;
import com.omarkanteh.busbooking.dto.RegisterUserRequest;
import com.omarkanteh.busbooking.dto.UserDto;
import com.omarkanteh.busbooking.entities.ResetPassword;
import com.omarkanteh.busbooking.entities.User;
import com.omarkanteh.busbooking.enums.UserRole;
import com.omarkanteh.busbooking.mappers.UserMapper;

import com.omarkanteh.busbooking.repositories.ResetPasswordRepository;
import com.omarkanteh.busbooking.repositories.UserRepository;
import com.omarkanteh.busbooking.repositories.VerificationTokenRepository;
import com.omarkanteh.busbooking.services.EmailService;
import com.omarkanteh.busbooking.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final ResetPasswordRepository resetPasswordRepository;

    // Retrieves all users with optional sorting by name or email
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "", name = "sort") String sort) {
        if (!Set.of("firstName", "email").contains(sort)) {
            sort = "firstName";
        }
        return userRepository.findAll(Sort.by(sort))
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
    // Fetches a user by ID; returns 404 if the user is not found
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id==authentication.principal.id")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toDto(user));
    }
    // Registers a new user; returns an error if the email is already in use
    @PostMapping
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        if (userRepository.existsByEmail((request.getEmail()))) {
            return ResponseEntity.badRequest().body(
                    Map.of("email",  "Email is already registered")
            );
        }
        User user =  userMapper.toEntity(request);

        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        var verificationToken = tokenService.generateVerificationToken(user);


        emailService.sendConfirmationEmail(verificationToken.getUser() ,verificationToken.getToken());
        UserDto userDto = userMapper.toDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }
    // Updates an existing user's information by ID
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id==authentication.principal.id")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody UserDto request
    ) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userMapper.UpdateUserRequest(request, user);
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(user));
    }
    // Deletes a user by ID; returns 404 if the user is not found
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
//    // Changes a user's password by ID; validates current password
//    @PostMapping("{id}/change-password")
//    @PreAuthorize("#id==authentication.principal.id")
//    public ResponseEntity<Void> changePassword(
//            @PathVariable Long id,
//            @RequestBody ChangePasswordRequest request
//    ) {
//        User user = userRepository.findById(id).orElse(null);
//        if (user == null) {
//            return ResponseEntity.notFound().build();
//        }
//        if(!user.getPassword().equals(request.getOldPassword())) {
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        userRepository.save(user);
//        return ResponseEntity.noContent().build();
//    }

}

