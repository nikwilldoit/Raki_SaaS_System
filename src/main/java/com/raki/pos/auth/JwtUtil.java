package com.raki.pos.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class for handling JSON Web Tokens (JWT).
 * <p>
 * This class provides methods to generate, validate, and extract information
 * from JWTs. It uses the JJWT library for all token operations.
 * </p>
 */
@Component
public class JwtUtil {


    private static final Logger logger = LogManager.getLogger(JwtUtil.class);


    /**
     * The secret key for signing JWT tokens.
     */
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    /**
     * Generates a new JWT token for a specific user based on their email.
     *
     * @param email The user's email address to be included as the subject of the token.
     * @return A signed JWT string containing the email, creation date, and expiration date.
     */
    public String generateToken(String email) {
        logger.debug("Generating token for {}", email);
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
        logger.debug("Token generated: {}", token);
        return token;
    }

    /**
     * Validates a token against a specific email address.
     *
     * @param token The JWT string to validate.
     * @param email The email address to compare against the token's subject.
     * @return true if the token belongs to the email and is not expired; false otherwise.
     */
    public boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        boolean valid = (extractedEmail.equals(email) && !isTokenExpired(token));
        logger.debug("Token validity: {}", valid);
        return valid;
    }

    /**
     * Extracts the email address (subject) from the JWT.
     *
     * @param token The JWT string.
     * @return The email address contained in the token.
     */
    public String extractEmail(String token) {
        logger.debug("Extracting email from token");
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT.
     *
     * @param token The JWT string.
     * @return The expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic helper method to extract a specific claim from the token.
     *
     * @param token          The JWT string.
     * @param claimsResolver A function to extract the desired claim from the Claims object.
     * @param <T>            The type of the claim being extracted.
     * @return The extracted claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT and retrieves all claims (the payload data).
     * <p>
     * This method uses the secret key to verify the signature. If the signature
     * is invalid or the token is tampered with, this will throw an exception.
     * </p>
     *
     * @param token The JWT string.
     * @return A Claims object containing all data inside the token.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.warn("Failed to parse JWT", e);
            throw e;
        }
    }

    /**
     * Checks if the token has expired.
     *
     * @param token The JWT string.
     * @return true if the current time is after the token's expiration date.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Decodes the SECRET string into a cryptographic Key object.
     *
     * @return The HMAC-SHA key used for signing.
     */
    private Key getSignKey() {
        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(SECRET);
        } catch (Exception e) {
            logger.error("JWT SECRET is NOT Base64 encoded! Fix the SECRET!", e);
            throw e;
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}