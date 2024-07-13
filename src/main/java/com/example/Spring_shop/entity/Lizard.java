package com.example.Spring_shop.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Lizard {
    private String image;
    private String subject;
    private String url;
}

