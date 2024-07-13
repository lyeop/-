package com.example.Spring_shop.repository;

import com.example.Spring_shop.entity.KakaoUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoRepository extends JpaRepository<KakaoUserEntity, String> {

}
