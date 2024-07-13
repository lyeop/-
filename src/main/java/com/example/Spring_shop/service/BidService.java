package com.example.Spring_shop.service;

import com.example.Spring_shop.dto.BidRequest;
import com.example.Spring_shop.entity.Bid;

import com.example.Spring_shop.entity.Item;
import com.example.Spring_shop.repository.BidRepository;

import com.example.Spring_shop.repository.ItemRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class BidService {

    private final BidRepository bidRepository;

    private final ItemRepository itemRepository;


    public int getCurrentBidPrice(Long itemId) {
        // 여기서는 데이터베이스나 외부 시스템에서 최신 입찰가를 가져와 반환하는 로직을 작성합니다.
        // 예를 들어, 데이터베이스에서 최신 입찰가를 조회하는 코드를 작성할 수 있습니다.
        // 임시로 15000을 반환하도록 작성하겠습니다.
        return getHighestBidPrice(itemId); // 실제로는 데이터베이스에서 조회한 값을 반환해야 합니다.
    }
    public int getLowcurrendBidprice(Long itemId){
        if(getSLowestBidPriceByItemId(itemId)==0){
            return getStartingBidPrice(itemId);
        }
        else{
            return getSLowestBidPriceByItemId(itemId);
        }

    }

    public int placeBid(Long itemId,BidRequest bidRequest, String username) {
        // 입찰 처리 로직
        // 예시로 새로운 입찰을 저장하는 코드를 작성합니다.
        System.out.println(bidRequest.getItemId());
        System.out.println("입찰"+bidRequest.getBidPrice());
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityExistsException::new);
        if(bidRequest.getBidPrice()>item.getBidPrice()) {
            item.setBidPrice(bidRequest.getBidPrice());
            item.updateBidPrice(bidRequest.getBidPrice());
        }

        // 해당 아이템의 최고 입찰가를 반환하도록 수정합니다.
        return getHighestBidPrice(bidRequest.getItemId());
    }
    public int lowPlaceBid(Long itemId,BidRequest bidRequest, String username) {
        // 시작 입찰가
        // 예시로 새로운 입찰을 저장하는 코드를 작성합니다.
        System.out.println(bidRequest.getItemId());

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityExistsException::new);
        item.setLowestBidPrice(bidRequest.getLowestBidPrice());
        item.updateLowestBidPrice(bidRequest.getLowestBidPrice());


        // 해당 아이템의 최고 입찰가를 반환하도록 수정합니다.
        return getSLowestBidPriceByItemId(bidRequest.getItemId());
    }

    // 특정 아이템의 최고 입찰가를 조회하는 메서드
    public int getHighestBidPrice(Long itemId) {
        Integer bidPrice = itemRepository.findBidPriceByItemId(itemId);
        return (bidPrice != null) ? bidPrice : 0;
    }
    public int getSLowestBidPriceByItemId(Long itemId) {
        Integer LowestBidPrice=  itemRepository.findLowestBidPriceByItemId(itemId);
        return (LowestBidPrice != null) ? LowestBidPrice : 0;
    }


    public void updateItemLowestBidPrice(Long itemId, int bidPrice){
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityExistsException::new);
        if(item.getBidPrice()>bidPrice){
            item.setPrice(item.getBidPrice());
        }else{
            item.setBidPrice(bidPrice);
            item.updateBidPrice(bidPrice);
        }
    }

    public int getStartingBidPrice(Long itemId) {
        Integer StartingBidPrice=  itemRepository.findStartingBidPriceByItemId(itemId);
        return (StartingBidPrice != null) ? StartingBidPrice : 0;
    }


}
