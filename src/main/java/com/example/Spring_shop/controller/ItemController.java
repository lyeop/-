package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.ItemFormDto;
import com.example.Spring_shop.dto.ItemSearchDto;
import com.example.Spring_shop.entity.BidData;
import com.example.Spring_shop.entity.Item;
import com.example.Spring_shop.service.BidService;
import com.example.Spring_shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final BidService bidService;


    // 새로운 상품 등록 폼을 보여줍니다.
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    // 새로운 상품을 등록합니다.
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        // 입력 값 검증에서 오류가 발생하면 다시 상품 등록 폼을 보여줍니다.
        if (bindingResult.hasErrors()){
            return "/item/itemForm";
        }

        // 첫 번째 상품 이미지는 필수 입력 값입니다.
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "/item/itemForm";
        }
        try {
            // 상품 정보를 저장합니다.
            itemService.saveItem(itemFormDto, itemImgFileList);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "/item/itemForm";
        }
        return "redirect:/";
    }

    // 상품 상세 정보를 조회합니다.
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){
        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        }
        catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto",new ItemFormDto());

            return "/item/itemForm";
        }
        return "/item/itemForm";
    }

    // 상품 정보를 수정합니다.
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model){

        // 입력 값 검증에서 오류가 발생하면 다시 상품 수정 폼을 보여줍니다.
        if (bindingResult.hasErrors()){
            return "/item/itemForm";
        }
        // 첫 번째 상품 이미지는 필수 입력 값입니다.
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "/item/itemForm";
        }
        try {
            // 상품 정보를 수정합니다.
            itemService.updateItem(itemFormDto, itemImgFileList);
        }
        catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/"; // 다시 실행 /
    }

    //value 2개인 이유
    //1. 네비게이션에서 상품관리 클릭하면 나오는거
    //2. 상품관리안에서 페이지 이동할때 받는거
    // 관리자 페이지에서 상품 목록을 관리합니다.
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){
        // page.isPresent() -> page 값 있어?
        // 어 값 있어 page.get() 아니 값 없어 0
        // 페이지당 사이즈 5 -> 5개만 나옵니다. 6개 되면 페이지 바뀜
        // 페이지 번호가 있으면 해당 페이지를, 없으면 0 페이지를 요청합니다.
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);

        // 상품 목록을 조회합니다.
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "/item/itemMng";
    }

    // 상품 상세 정보를 조회합니다.
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId")Long itemId){

        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);

        return "/item/itemDtl";
    }


}
