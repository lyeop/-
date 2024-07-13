package com.example.Spring_shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CartDetailDto {
    private Long cartItemId;
    private String itemNm;
    private int price;
    private int count;
    private String imgUrl;
    private int BidPrice;
    private int lowestBidPrice;
    private LocalDateTime endDate;


    public CartDetailDto(Long cartItemId, String itemNm, int price, int count, String imgUrl
            ,int BidPrice,int lowestBidPrice, LocalDateTime endDate) {
        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.count =count;
        this.imgUrl = imgUrl;
        this.BidPrice=BidPrice;
        this.lowestBidPrice = lowestBidPrice;
        this.endDate=endDate;
    }


}
