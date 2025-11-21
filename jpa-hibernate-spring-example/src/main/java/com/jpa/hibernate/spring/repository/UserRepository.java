package com.jpa.hibernate.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jpa.hibernate.spring.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String mail);

    // DTO projection (interface-based) example for lighter queries
    @Query("select u.id as id, u.name as name, u.email as email from User u where u.mail = :email")
    Optional<UserProjection> findProjectionByEmail(@Param("email") String email);

    interface UserProjection {
        Long getId();
        String getName();
        String getEmail();
    }
    
}
