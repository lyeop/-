package com.example.Spring_shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequest {
    private Long itemId;
    private int bidPrice;
    private int lowestBidPrice;
}