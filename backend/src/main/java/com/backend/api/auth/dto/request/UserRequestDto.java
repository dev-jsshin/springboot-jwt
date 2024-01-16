package com.backend.api.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class UserRequestDto {

    @Getter
    @Setter
    public static class SignUp {

        @NotEmpty(message = "Please enter your user_id")
        private String user_id;

        @NotEmpty(message = "Please enter your password")
        private String password;
    }

    @Getter
    @Setter
    public static class Login {
        @NotEmpty(message = "Please enter your user_id")
        private String user_id;

        @NotEmpty(message = "Please enter your password")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(user_id, password);
        }
    }

    @Getter
    @Setter
    public static class Reissue {
        @NotEmpty(message = "Please enter your accessToken")
        private String accessToken;

        @NotEmpty(message = "Please enter your refreshToken")
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class Logout {
        @NotEmpty(message = "Bad request")
        private String accessToken;

        @NotEmpty(message = "Bad request")
        private String refreshToken;
    }
}
