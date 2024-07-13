package com.example.Spring_shop.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerCenterPostDto {

    private Long id;
    private String writer;
    private String title;
    private String content;

    // 생성자, getter, setter
    public CustomerCenterPostDto(Long id, String writer, String title, String content) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
    }
}