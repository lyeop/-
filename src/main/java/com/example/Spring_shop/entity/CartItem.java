package com.example.Spring_shop.entity;

import com.example.Spring_shop.constant.ItemValue;
import com.example.Spring_shop.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name="cart_item")
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int BidPrice;
    private int count;
    private int lowestBidPrice;

    public static CartItem createCartItem(Cart cart, Item item, int BidPrice,int lowestBidPrice){
        CartItem cartItem =new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(1);
        cartItem.setBidPrice(BidPrice);
        cartItem.setLowestBidPrice(lowestBidPrice);


        return cartItem;
    }
    public void addCount(int count){
        this.count =count;
    }

    public void updateCount(int count){
        this.count=count;
    }
    public void updateBidPrice(int BidPrice){
        int restStock = BidPrice - this.BidPrice ;
        this.BidPrice=BidPrice;
    }
    public void updateLowestBidPrice(int lowestBidPrice){
        this.lowestBidPrice=lowestBidPrice;
    }
}
