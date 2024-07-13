package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.*;
import com.example.Spring_shop.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final HttpSession httpSession;

    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                         BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = getEmailFromPrincipalOrSession(principal);
        Long cartItemId;
        try {
            cartItemId = cartService.addCart(cartItemDto, email);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }



    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        String email = getEmailFromPrincipalOrSession(principal);
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(email);
        model.addAttribute("cartItems", cartDetailDtoList);
        return "cart/cartList";
    }


    @PatchMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       @RequestParam("bidPrice")int bidPrice, Principal principal) {
        if (bidPrice <= 0) {
            return new ResponseEntity<>("최소 1개이상 담아주세요.", HttpStatus.BAD_REQUEST);
        }

        String email = getEmailFromPrincipalOrSession(principal);
        if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemlowestBidPrice(cartItemId, bidPrice);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }
    @PatchMapping(value = "/bid/cart/{cartItemId}")
    public @ResponseBody ResponseEntity updateCartBidPrice(
            @PathVariable("cartItemId") Long cartItemId,
            @RequestParam("bidPrice") int bidPrice, // 이 부분에서 'bidPrice'라는 이름으로 매개변수를 받습니다.
            @RequestParam("lowestBidPrice") int lowestBidPrice,
            Principal principal) {
        System.out.println(bidPrice);
        System.out.println(lowestBidPrice);
        String email = getEmailFromPrincipalOrSession(principal);
        if (!cartService.validateCartItem(cartItemId,email)){
            return new ResponseEntity<>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.updateCartItemlowestBidPrice(cartItemId,lowestBidPrice);
        cartService.updateCartItemBidPrice(cartItemId,bidPrice);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @DeleteMapping(value = "/cartItem/{cartItemId}")
    public @ResponseBody ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                                       Principal principal) {
        String email = getEmailFromPrincipalOrSession(principal);
        if (!cartService.validateCartItem(cartItemId, email)) {
            return new ResponseEntity<>("수정권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                                      Principal principal){
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        if (cartOrderDtoList == null || cartOrderDtoList.size() == 0) {
            return new ResponseEntity<>("주문할 상품을 선택해주세요.", HttpStatus.FORBIDDEN);
        }

        String email = getEmailFromPrincipalOrSession(principal);
        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if (!cartService.validateCartItem(cartOrder.getCartItemId(), email)) {
                return new ResponseEntity<>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }

        Long orderId;
        try {
            orderId = cartService.orderCartItem(cartOrderDtoList, email);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }
}