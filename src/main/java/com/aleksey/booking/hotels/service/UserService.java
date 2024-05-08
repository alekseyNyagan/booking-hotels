package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.request.UpsertUserRequest;
import com.aleksey.booking.hotels.api.response.UserResponse;

public interface UserService {

    UserResponse findById(Long id);

    UserResponse create(UpsertUserRequest upsertUserRequest);

    UserResponse update(Long id, UpsertUserRequest upsertUserRequest);

    void delete(Long id);
}