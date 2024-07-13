package com.example.Spring_shop.repository;

import com.example.Spring_shop.dto.ItemSearchDto;
import com.example.Spring_shop.dto.MainItemDto;
import com.example.Spring_shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}

