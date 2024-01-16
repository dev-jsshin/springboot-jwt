package com.backend.api.auth.service;

import com.backend.api.auth.dto.request.UserRequestDto;
import com.backend.api.auth.dto.response.UserResponseDto;
import com.backend.api.auth.entity.Users;
import com.backend.api.auth.enums.Authority;
import com.backend.api.auth.mapper.UserMapper;
import com.backend.module.common.dto.DataResponseDto;
import com.backend.module.common.enums.StatusCode;
import com.backend.module.common.exception.GeneralException;
import com.backend.module.config.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UserMapper userMapper;

    public DataResponseDto<Object> signUp(UserRequestDto.SignUp signUp) {

        if (userMapper.existsByUserId(signUp.getUser_id()) > 0) {
            throw new GeneralException(StatusCode.BAD_REQUEST, "This account is already subscribed");
        }

        Users user = Users.builder()
                          .user_id(signUp.getUser_id())
                          .password(passwordEncoder.encode(signUp.getPassword()))
                          .auth_type(Authority.US_AUTH_TYPE.getValue())
                          .build();

        userMapper.save(user);

        return DataResponseDto.of(StatusCode.OK);
    }

    public DataResponseDto<Object> login(UserRequestDto.Login login) {

        if (userMapper.findByUserId(login.getUser_id()).orElse(null) == null) {
            throw new GeneralException(StatusCode.BAD_REQUEST, "Not found userId");
        }

        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

        // Id Pwd Check
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Create JWT
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // RefreshToken Redis Save (expirationTime Auto Delete)
        redisTemplate.opsForValue()
                      .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.SECONDS);

        return DataResponseDto.of(tokenInfo);
    }

    public DataResponseDto<Object> reissue(UserRequestDto.Reissue reissue) {

        // Refresh Token Check
        if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
            throw new GeneralException(StatusCode.BAD_REQUEST, "Invalid Refresh Token");
        }

        // Get userId from Access Token
        Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());

        // Get Refresh Token From Redis
        String refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());

        // If a Refresh Token does not exist due to a logout
        if(ObjectUtils.isEmpty(refreshToken)) {
            throw new GeneralException(StatusCode.BAD_REQUEST, "Invalid Refresh Token");
        }

        if(!refreshToken.equals(reissue.getRefreshToken())) {
            throw new GeneralException(StatusCode.BAD_REQUEST, "Invalid Refresh Token");
        }

        // Create a new token
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // Redis Update
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return DataResponseDto.of(tokenInfo);
    }

    public DataResponseDto<Object> logout(UserRequestDto.Logout logout) {
        // Check Access Token
        if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
            throw new GeneralException(StatusCode.BAD_REQUEST, "Bad request");
        }

        // Get userId from Access Token
        Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());

        // Find Refresh Token From Redis
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token Delete
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // Access Token BlackList Save
        Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
        redisTemplate.opsForValue()
                     .set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

        return DataResponseDto.of(StatusCode.OK);
    }
}
