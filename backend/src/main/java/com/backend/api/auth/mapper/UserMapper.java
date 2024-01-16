package com.backend.api.auth.mapper;

import com.backend.api.auth.entity.Users;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Mapper
@Repository
public interface UserMapper {

    Optional<Users> findByUserId(String userId);

    int existsByUserId(String userId);

    void save(Users user);
}
