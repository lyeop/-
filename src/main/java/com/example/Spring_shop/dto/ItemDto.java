package com.example.Spring_shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDto {
    private Long id;
    private String itemNm;
    private Integer price;
    private String itemDetail;
    private String sellStatCd;
    private String itemValue;
    private Integer BidPrice;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;
    private Integer startingBidPrice;

}
