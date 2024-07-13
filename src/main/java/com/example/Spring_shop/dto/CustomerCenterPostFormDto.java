package com.example.Spring_shop.dto;


import com.example.Spring_shop.entity.CustomerCenterPost;
import com.example.Spring_shop.entity.Item;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class CustomerCenterPostFormDto {

    private Long id;

    @NotBlank(message = "작성자는 필수 입력 값 입니다.")
    private String writer;

    @NotNull(message = "제목은 필수 입력 값 입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값 입니다.")
    private String content;

    private Long memberId; // 회원 ID 추가

    //------------------------------------------------------------------------//

    // ModelMapper
    private static ModelMapper modelMapper =new ModelMapper();

    public CustomerCenterPost createCustomerCenterPost(){


        return modelMapper.map(this, CustomerCenterPost.class);
    }

    public static CustomerCenterPostFormDto of(CustomerCenterPost customerCenterPost){ //수정하기 위한 모델맵퍼 연결
        return modelMapper.map(customerCenterPost,CustomerCenterPostFormDto.class);
    }

}
