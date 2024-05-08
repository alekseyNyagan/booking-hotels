package com.aleksey.booking.hotels.repository;

import com.aleksey.booking.hotels.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findUserByName(String name);

  boolean existsByNameAndEmail(String name, String email);
}