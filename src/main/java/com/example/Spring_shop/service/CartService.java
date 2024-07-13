package com.example.Spring_shop.service;

import com.example.Spring_shop.dto.*;
import com.example.Spring_shop.entity.*;
import com.example.Spring_shop.exception.OutOfStockException;
import com.example.Spring_shop.repository.*;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;
    private final BidService bidService;
    public Long addCart(CartItemDto cartItemDto, String email){
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityExistsException::new);
        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null ){
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(),item.getId());
        if(savedCartItem != null){
            savedCartItem.updateBidPrice(cartItemDto.getBidPrice());
            savedCartItem.updateCount(cartItemDto.getCount());
            savedCartItem.updateLowestBidPrice(cartItemDto.getBidPrice());
            return savedCartItem.getId();
        }
        else{
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getLowestBidPrice(), cartItemDto.getBidPrice());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        // email을 이용해서 Member 엔티티 객체 추출
        Member curMember = memberRepository.findByEmail(email);
        // cartItemId를 이용해서 CartItem 엔티티 객체 추출
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        // Cart -> Memeber 엔티티 객체를 추출
        Member savedMember = cartItem.getCart().getMember();
        // 현재 로그인된 Member == CartItem에 있는 Member -> 같지 않으면 true return false
        if(!StringUtils.equals(curMember.getEmail(),savedMember.getEmail())){
            return false;
        }
        // 현재 로그인된 Member == CartItem에 있는 Member -> 같으면 return true
        return true;
    }
    @Transactional
    public void updateCartItemlowestBidPrice(Long cartItemId, int lowestBidPrice){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        cartItem.updateLowestBidPrice(lowestBidPrice);
    }
    @Transactional
    public void updateCartItemBidPrice(Long cartItemId, int BidPrice){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        cartItem.updateLowestBidPrice(BidPrice);
    }

    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityExistsException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        // 주문DTO List 객체 생성
        List<OrderDto> orderDtoList = new ArrayList<>();
        // 카트 주문 List에 있는 목록 -> 카트 아이템 객체로 추출
        // 주문 Dto에 CartItem 정보를 담고
        // 위에 선언된 주문 Dto List에 추가
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityExistsException::new);
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }
        // 주문DTO리스트 현재 로그인된 이메일 매개변수 넣고
        // 주문 서비스 실행 -> 주문
        Long orderId = orderService.orders(orderDtoList, email);

        //Cart에서 있던 Item 주문이 되니까 CartItem 모두 삭제
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityExistsException::new);
            cartItemRepository.delete(cartItem);
        }
        return orderId;
    }

}
