package com.example.Spring_shop.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidData {
    private int bidPrice;
    private int lowesBidPrice;
    private int startingBidPrice;

    public BidData(int bidPrice,int startingBidPrice) {
        this.bidPrice = bidPrice;
        this.startingBidPrice=startingBidPrice;
    }

}