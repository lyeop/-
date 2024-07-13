package com.example.Spring_shop.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;


@Getter
@Setter

public class MemberUpdateFormDto {

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String tel;

}
