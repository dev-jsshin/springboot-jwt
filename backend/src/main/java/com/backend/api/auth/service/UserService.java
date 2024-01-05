package com.backend.api.auth.service;

import com.backend.api.auth.dto.request.UserRequestDto;
import com.backend.api.auth.dto.response.UserResponseDto;
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
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    UserMapper userMapper;

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
}
