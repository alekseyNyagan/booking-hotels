package com.aleksey.booking.hotels.service;

import com.aleksey.booking.hotels.api.request.UpsertUserRequest;
import com.aleksey.booking.hotels.api.response.UserResponse;
import com.aleksey.booking.hotels.mapper.UserMapper;
import com.aleksey.booking.hotels.model.User;
import com.aleksey.booking.hotels.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserResponse findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        user.orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("Пользователь с id {0} не найден!", id)));
        return userMapper.toDto(user.get());
    }

    @Override
    public UserResponse update(Long id, UpsertUserRequest upsertUserRequest) {
        User user = userMapper.toEntity(id, upsertUserRequest);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}