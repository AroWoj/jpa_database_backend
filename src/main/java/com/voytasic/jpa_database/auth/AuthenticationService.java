package com.voytasic.jpa_database.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voytasic.jpa_database.email.EmailService;
import com.voytasic.jpa_database.email.EmailTemplateName;
import com.voytasic.jpa_database.repository.ActivateTokenRepository;
import com.voytasic.jpa_database.repository.UserRepository;
import com.voytasic.jpa_database.token.Token;
import com.voytasic.jpa_database.repository.entity.ActivateToken;
import com.voytasic.jpa_database.token.Token;
import com.voytasic.jpa_database.token.TokenRepository;
import com.voytasic.jpa_database.repository.entity.User;
import com.voytasic.jpa_database.role.RoleRepository;
import com.voytasic.jpa_database.security.JwtService;
import com.voytasic.jpa_database.token.TokenType;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

     private final RoleRepository roleRepository;
     private final PasswordEncoder passwordEncoder;
     private final UserRepository userRepository;
     private final TokenRepository tokenRepository;
     private final ActivateTokenRepository activateTokenRepository;
     private final EmailService emailService;
     private final AuthenticationManager authenticationManager;
     private final JwtService jwtService;

     @Value("${application.mailing.frontend.activation-url}")
     private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(()->new IllegalStateException("ROLE USER was not initialized"));
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendMail(
                user.getEmail(),
                user.getName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation code"
        );
    }
    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = ActivateToken.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        activateTokenRepository.save(token);
        return generatedToken;
    }
    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("name", user.getName());
        var jwtToken = jwtService.generateToken(claims, user);
        var refreshToken = jwtService.generateRefreshToken(claims, user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);

    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }





    //@Transactional
    public void activateAccount(String token) throws MessagingException {
    ActivateToken savedToken = activateTokenRepository.findByToken(token)
            .orElseThrow(()-> new RuntimeException("Invalid token"));
    if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
        sendValidationEmail(savedToken.getUser());
        throw new RuntimeException("Activation token has expired. New token has been sent to the same email address.");
    }

    var user = userRepository.findById(savedToken.getUser().getId())
            .orElseThrow(()-> new UsernameNotFoundException("User not found"));

    user.setEnabled(true);
    userRepository.save(user);
    savedToken.setValidatedAt(LocalDateTime.now());
    activateTokenRepository.save(savedToken);

    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){

            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null ) {
            var user = userRepository.findByEmail(userEmail).orElseThrow();

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                // to do revoke token
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);


                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }

    }
}
