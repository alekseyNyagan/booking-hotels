package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.request.UpsertUserRequest;
import com.aleksey.booking.hotels.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class UserMapperDelegate implements UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User toEntity(UpsertUserRequest upsertUserRequest) {
        User user = new User();
        user.setName(upsertUserRequest.name());
        user.setPassword(passwordEncoder.encode(upsertUserRequest.password()));
        user.setEmail(upsertUserRequest.email());
        user.setRoles(upsertUserRequest.roles());
        return user;
    }

    @Override
    public User toEntity(Long userId, UpsertUserRequest upsertUserRequest) {
        User user = toEntity(upsertUserRequest);
        user.setId(userId);
        return user;
    }
}