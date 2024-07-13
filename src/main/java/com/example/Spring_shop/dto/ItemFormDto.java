package com.example.Spring_shop.dto;

import com.example.Spring_shop.constant.ItemSellStatus;
import com.example.Spring_shop.constant.ItemValue;
import com.example.Spring_shop.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {
    private  Long id;

    private int startingBidPrice;

    private int lowestBidPrice;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String itemDetail;

    private Integer stockNumber = 1;

    @NotNull(message = "마감일는 필수 입력 값입니다.")
    private LocalDateTime endDate;

    @NotNull(message = "경매 시작가는 필수 입력 값입니다.")
    private Integer BidPrice;

    private ItemSellStatus itemSellStatus;

    private ItemValue itemValue; // 상품의 종류
    //-------------------------------------------------------------------
    //ItemImg

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>(); // 상품 이미지 정보

    private List<Long> itemImgIds = new ArrayList<>(); //상품 이미지 아이디

    //--------------------------------------------------------------------
    // ModelMapper
    private static ModelMapper modelMapper =new ModelMapper();

    public Item createItem(){
        //ItemDto -> Item 연결
        return modelMapper.map(this, Item.class);
    }
    public static ItemFormDto of(Item item){ //수정하기 위한 모델맵퍼 연결
        //Item -> ItemDto 연결
        return modelMapper.map(item,ItemFormDto.class);
    }
}
