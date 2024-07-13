package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.BidRequest;
import com.example.Spring_shop.dto.SessionUser;
import com.example.Spring_shop.entity.BidData;
import com.example.Spring_shop.service.BidService;
import com.example.Spring_shop.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BidController {
    private final BidService bidService; // 비즈니스 로직을 처리할 서비스 클래스
    private final HttpSession httpSession;


    @GetMapping(value = {"/fetchBidData/{itemId}","/cart/fetchBidData","/item/**"})
    public BidData fetchBidData(@PathVariable("itemId")Long itemId) {
        int currentBidPrice = bidService.getCurrentBidPrice(itemId);
        int lowBidPirce = bidService.getLowcurrendBidprice(itemId);
        return new BidData(currentBidPrice,lowBidPirce);
    }

    @PatchMapping(value = {"/bid/{itemId}"})
    public @ResponseBody ResponseEntity<?> placeBid(@PathVariable("itemId") Long itemId, @RequestBody BidRequest bidRequest, Principal principal) {
        // Check if the itemId in the path matches the itemId in the body
        if (!itemId.equals(bidRequest.getItemId())) {
            return ResponseEntity.badRequest().body("Item ID in the path and body do not match.");
        }
        String email = getEmailFromPrincipalOrSession(principal);

        int newBidPrice = bidService.placeBid(itemId,bidRequest, email);
        int currentBidPrice = bidService.lowPlaceBid(itemId,bidRequest, email);

        if (bidRequest.getBidPrice() <= currentBidPrice) {

            return ResponseEntity.badRequest().body("최고 입찰가 보다 입찰 가격이 낮습니다.");
        }else{
            bidService.updateItemLowestBidPrice(itemId, newBidPrice);
            Map<String, Object> response = new HashMap<>();
            response.put("newBidPrice", newBidPrice);
            return ResponseEntity.ok(response);
        }
    }

    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }

}
