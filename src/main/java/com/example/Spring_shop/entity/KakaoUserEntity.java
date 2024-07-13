package com.example.Spring_shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserEntity {
    @Id
    private String userid;
    private String pwd;
    private String name;
    private String email;
    private String gender;
    private String birth;
}
