package com.aleksey.booking.hotels.mapper;

import com.aleksey.booking.hotels.api.request.UpsertUserRequest;
import com.aleksey.booking.hotels.api.response.UserResponse;
import com.aleksey.booking.hotels.model.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UpsertUserRequest upsertUserRequest);

    @Mapping(source = "userId", target = "id")
    User toEntity(Long userId, UpsertUserRequest upsertUserRequest);

    UserResponse toDto(User user);
}