package com.example.Spring_shop.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MainItemDto {
    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;
    private LocalDateTime endDate;
    private Integer BidPrice;


    @QueryProjection // Querydsl 결과 조회 시 MainItemDto 객체로 바로 오도록 활용
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price, LocalDateTime endDate,
                       Integer BidPrice){
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
        this.endDate = endDate;
        this.BidPrice = BidPrice;

    }
}
