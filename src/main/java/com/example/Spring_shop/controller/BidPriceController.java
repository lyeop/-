package com.example.Spring_shop.controller;

import com.example.Spring_shop.service.CartService;
import com.example.Spring_shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class BidPriceController {
    private final ItemService itemService;


    @GetMapping("/{cartItemId}/currentBidPrice")
    public ResponseEntity<Integer> getCurrentBidPrice(@PathVariable Long cartItemId) {
        try {
            int currentBidPrice = itemService.getCurrentBidPriceByCartItemId(cartItemId); // 서비스에서 최고 입찰가 조회
            return ResponseEntity.ok(currentBidPrice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("/{cartItemId}/updateBidPrice")
    public ResponseEntity<Void> updateHighestBidPrice(@PathVariable Long cartItemId, @PathVariable int newBidPrice) {
        System.out.println(newBidPrice);
        try {
            itemService.updateHighestBidPrice(cartItemId, newBidPrice);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
