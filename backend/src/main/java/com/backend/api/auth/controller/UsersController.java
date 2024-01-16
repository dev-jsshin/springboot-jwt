package com.backend.api.auth.controller;

import com.backend.api.auth.dto.request.UserRequestDto;
import com.backend.api.auth.service.UserService;
import com.backend.module.common.dto.DataResponseDto;
import com.backend.module.common.enums.StatusCode;
import com.backend.module.common.exception.GeneralException;
import com.backend.module.config.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class UsersController {

    @Autowired
    UserService userService;

    @PostMapping("/sign-up")
    public DataResponseDto<Object> signUp(@Validated UserRequestDto.SignUp signUp, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> list = bindingResult.getFieldErrors();
            for(FieldError error : list) {
                throw new GeneralException(StatusCode.BAD_REQUEST, error.getDefaultMessage());
            }
        }

        return userService.signUp(signUp);
    }

    @PostMapping("/login")
    public DataResponseDto<Object> login(@Validated UserRequestDto.Login login, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> list = bindingResult.getFieldErrors();
            for(FieldError error : list) {
                throw new GeneralException(StatusCode.BAD_REQUEST, error.getDefaultMessage());
            }
        }

        return userService.login(login);
    }

    @PostMapping("/reissue")
    public DataResponseDto<Object> reissue(@Validated UserRequestDto.Reissue reissue, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> list = bindingResult.getFieldErrors();
            for(FieldError error : list) {
                throw new GeneralException(StatusCode.BAD_REQUEST, error.getDefaultMessage());
            }
        }

        return userService.reissue(reissue);
    }

    @PostMapping("/logout")
    public DataResponseDto<Object> logout(@Validated UserRequestDto.Logout logout, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<FieldError> list = bindingResult.getFieldErrors();
            for(FieldError error : list) {
                throw new GeneralException(StatusCode.BAD_REQUEST, error.getDefaultMessage());
            }
        }

        return userService.logout(logout);
    }
}
