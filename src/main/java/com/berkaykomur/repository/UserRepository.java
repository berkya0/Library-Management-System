package com.berkaykomur.repository;

import com.berkaykomur.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.isActive=true")
    Optional<User> findByUsername(String username);

    boolean existsByUsernameAndIsActiveTrue(String username);



}
