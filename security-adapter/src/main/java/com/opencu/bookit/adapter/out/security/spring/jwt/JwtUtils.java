package com.opencu.bookit.adapter.out.security.spring.jwt;

import com.opencu.bookit.adapter.out.security.spring.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for generating, parsing, and validating JSON Web Tokens (JWT) used for authentication.
 * <p>
 *      This class handles JWT operations such as creating tokens with user identity, extracting information
 *      from tokens, and verifying token integrity and validity.
 * </p>
 * It uses a secret key for signing tokens with the HS512 algorithm and manages the token expiration period.
 * Logging is done for various token validation exceptions to help diagnose authentication errors.
 * *
 **/
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${bookit.app.jwtSecret}")
    private String jwtSecretString;

    @Value("${bookit.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private SecretKey jwtSecret;

    @PostConstruct
    public void init() {
        this.jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * Generates a JWT token for the authenticated user.
     * The token subject contains the Telegram user ID.
     * The token is signed and contains issued and expiration timestamps.
     *
     * @param authentication the Authentication object containing user details
     * @return a signed JWT token string
     **/
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getTgId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }


    public Long getTgIdFromJwtToken(String token) {
        String subject = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.valueOf(subject);
    }

    /**
     * Validates the JWT token’s integrity, format, and expiration.
     * Logs specific errors in case of invalid signature, malformed token, expiration, unsupported token,
     * or empty claims.
     *
     * @param authToken the JWT token string to validate
     * @return true if valid, false otherwise
     **/
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}