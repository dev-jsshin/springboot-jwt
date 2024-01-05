package com.backend.api.auth.service;

import com.backend.api.auth.entity.Users;
import com.backend.api.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Users user = userMapper.findByUserId(userId)
                         .orElseThrow(() -> new UsernameNotFoundException("Not Found User"));

        return user.builder()
                   .user_id(user.getUser_id())
                   .password(user.getPassword())
                   .auth_type(user.getAuth_type())
                   .build();
    }
}
