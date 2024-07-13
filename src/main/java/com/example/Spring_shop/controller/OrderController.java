package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.CartDetailDto;
import com.example.Spring_shop.dto.OrderDto;
import com.example.Spring_shop.dto.OrderHistDto;
import com.example.Spring_shop.dto.SessionUser;
import com.example.Spring_shop.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final HttpSession httpSession;

    @PostMapping(value = "/order")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,
                         Principal principal){
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(getErrorMessages(bindingResult), HttpStatus.BAD_REQUEST);
        }

        String email = getEmailFromPrincipalOrSession(principal);
        email = processSocialLoginEmail(email);
        if (email == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            Long orderId = orderService.order(orderDto, email);
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){
        String email = getEmailFromPrincipalOrSession(principal);
        if (email == null) {
            return "redirect:/login"; // 로그인 페이지로 리디렉션
        }

        email = processSocialLoginEmail(email);
        System.out.println(email);
        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(email, pageable);

        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal){
        String email = getEmailFromPrincipalOrSession(principal);
        if (email == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        if (!orderService.validateOrder(orderId, email)) {
            return new ResponseEntity<>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null && isSocialLogin(user)) {
            return user.getEmail();
        }
        return principal != null ? principal.getName() : null;
    }

    private String processSocialLoginEmail(String email) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null && isSocialLogin(user)) {
            String provider = user.getProvider();
            if ("google".equals(provider) || "naver".equals(provider) || "kakao".equals(provider)) {
                return user.getEmail();
            }
        }
        return email;
    }

    private boolean isSocialLogin(SessionUser user) {
        String provider = user.getProvider();
        return provider != null && ("google".equals(provider) || "naver".equals(provider) || "kakao".equals(provider));
    }

    private String getErrorMessages(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            sb.append(fieldError.getDefaultMessage());
        }
        return sb.toString();
    }
}
