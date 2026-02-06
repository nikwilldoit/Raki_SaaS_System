package com.raki.pos.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDTO {

    /**
     * Represents a login request containing the user's email and password.
     */
    @Setter
    @Getter
    @ToString
    public static class LoginRequest {

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    /**
     * Represents a login response containing the result of the authentication process.
     */
    @Getter
    @Setter // For tests
    public static class LoginResponse {

        private String username;
        private String token;

        /**
         * Constructs a LoginResponse object with the specified username and token.
         *
         * @param username the username retrieved from the database
         * @param token the JWT token generated upon successful login
         */
        public LoginResponse(String username, String token) {
            this.username = username;
            this.token = token;
        }
    }
}