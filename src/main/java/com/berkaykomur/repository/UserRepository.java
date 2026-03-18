package com.berkaykomur.repository;

import com.berkaykomur.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsernameAndIsActiveTrue(String username);
    boolean existsByUsernameAndIsActiveTrue(String username);



}
