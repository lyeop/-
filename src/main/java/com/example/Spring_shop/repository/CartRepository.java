package com.example.Spring_shop.repository;

import com.example.Spring_shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Cart findByMemberId(Long memberId);
}
