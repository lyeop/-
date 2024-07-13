package com.example.Spring_shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberEmailDto {
    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email
    private String email;

}