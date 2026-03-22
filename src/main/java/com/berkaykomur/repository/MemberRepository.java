package com.berkaykomur.repository;

import com.berkaykomur.model.Member;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	 @Query("SELECT m.id FROM Member m JOIN m.user u WHERE m.isActive=true and u.isActive=true " +
             "and u.username = :username")
	     Optional<Long> findMemberIdByUsername(@Param("username") String username);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM member", nativeQuery = true)
    void hardDeleteAll();
}

