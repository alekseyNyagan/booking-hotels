package com.aleksey.userservice.api.request;

public record UserRequestDTO(String username, String password, String email, String firstName, String lastName) {
}