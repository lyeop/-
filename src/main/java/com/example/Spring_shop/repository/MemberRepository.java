package com.example.Spring_shop.repository;

import com.example.Spring_shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
    Member findByTel(String tel);
    Optional<Member> findById(Long id);
}