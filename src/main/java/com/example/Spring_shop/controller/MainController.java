package com.example.Spring_shop.controller;

import com.example.Spring_shop.constant.ItemValue;
import com.example.Spring_shop.dto.ItemSearchDto;
import com.example.Spring_shop.dto.MainItemDto;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.service.ItemService;
import com.example.Spring_shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;
    private final MemberService memberService;

    // 메인 페이지를 처리하는 핸들러입니다.
    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model, Authentication authentication){
        // 페이지 요청을 처리하기 위한 Pageable 객체를 생성합니다.
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        // 메인 페이지에 표시할 상품 목록을 가져옵니다.
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
        // 테스트용 출력
        System.out.println(items.getNumber() + "!!!!!!!!!!!");
        System.out.println(items.getTotalPages() + "############");
        // 모델에 상품 목록과 검색 조건을 추가합니다.
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5); //페이지 최대 수


        return "main"; // main.html 뷰로 이동
    }
    @GetMapping(value = "/map")
    public String map(){
        return "api/map";
    }
    @GetMapping(value = "/item/items/{itemValue}")
    public String itemsByType(@PathVariable("itemValue") ItemValue itemValue, Optional<Integer> page, Model model, Authentication authentication) {
        ItemSearchDto itemSearchDto = new ItemSearchDto();
        itemSearchDto.setItemValue(itemValue);

        Pageable pageable = PageRequest.of(page.orElse(0), 5);
        Page<MainItemDto> items = itemService.getItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        model.addAttribute("selectedItemValue", itemValue);

        return "item/items";
    }
}
