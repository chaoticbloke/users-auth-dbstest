package com.phegondev.usersmanagementsystem.repository;


import com.phegondev.usersmanagementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String email);

    Optional<Object> findByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = :failedLoginAttempts WHERE u.username = :username")
    void updateFailedLoginAttempts(@Param("username") String username, @Param("failedLoginAttempts") Integer failedLoginAttempts);
}
