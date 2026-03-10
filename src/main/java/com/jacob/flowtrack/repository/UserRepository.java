package com.jacob.flowtrack.repository;

import com.jacob.flowtrack.entity.User;
import jakarta.persistence.Column;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Column(unique = true)
    Optional<User> findByEmail(String email);
}
