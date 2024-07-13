package com.example.Spring_shop.service;

import com.example.Spring_shop.constant.ItemValue;
import com.example.Spring_shop.dto.ItemFormDto;
import com.example.Spring_shop.dto.ItemImgDto;
import com.example.Spring_shop.dto.ItemSearchDto;
import com.example.Spring_shop.dto.MainItemDto;
import com.example.Spring_shop.entity.CartItem;
import com.example.Spring_shop.entity.Item;
import com.example.Spring_shop.entity.ItemImg;
import com.example.Spring_shop.repository.CartItemRepository;
import com.example.Spring_shop.repository.ItemImgRepository;
import com.example.Spring_shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final CartItemRepository cartItemRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품등록
        Item item = itemFormDto.createItem();

        itemRepository.save(item);

        // 이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if (i == 0)
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        // Entity
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        // DB 에서 데이터를 가지고 옵니다.
        // DTO
        List<ItemImgDto> itemImgDtoList = new ArrayList<>(); // 왜 DTO 만들었나요??

        for (ItemImg itemimg : itemImgList) {
            // Entity -> DTO
            ItemImgDto itemImgDto = ItemImgDto.of(itemimg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        // ITem -> ItemFormDto modelMapper
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 변경
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        //상품 이미지 변경
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public List<Item> findByItemValue(ItemValue itemValue) {
        return itemRepository.findByItemValue(itemValue);
    }

    @Transactional(readOnly = true) // 쿼리문 실행 읽기만 한다.
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getItemPage(itemSearchDto, pageable);
    }

    public int getCurrentBidPriceByCartItemId(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .map(CartItem::getBidPrice)
                .orElseThrow(() -> new IllegalArgumentException("Invalid cartItemId"));
    }
    public void updateHighestBidPrice(Long cartItemId, int newBidPrice) {
        cartItemRepository.findById(cartItemId).ifPresent(cartItem -> {
            cartItem.setBidPrice(newBidPrice);
            cartItemRepository.save(cartItem);
            updateItemHighestBidPrice(cartItem.getItem().getId());
        });
    }

    private void updateItemHighestBidPrice(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid itemId"));
        int highestBidPrice = itemRepository.findBidPriceByItemId(itemId);
        item.setBidPrice(highestBidPrice);
        itemRepository.save(item);
    }
}