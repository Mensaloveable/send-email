package com.project.app.repository;

import com.project.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByApiKey(String apiKey);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
