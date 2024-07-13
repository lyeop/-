package com.example.Spring_shop.repository;

import com.example.Spring_shop.entity.Bid;
import com.example.Spring_shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    // 추가적인 메서드가 필요한 경우 여기에 정의할 수 있습니다.
    @Query("SELECT MAX(b.bidPrice) FROM Bid b WHERE b.item.id = :itemId")
    Integer findBidPriceByItemId(@Param("itemId") Long itemId);

    @Query("SELECT b.startingBidPrice FROM Bid b WHERE b.item.id = :itemId")
    Integer findStartingBidPriceByItemId(@Param("itemId") Long itemId);

    @Query("SELECT b.lowestBidPrice FROM Bid b WHERE b.item.id = :itemId")
    Integer findLowestBidPriceByItemId(@Param("itemId") Long itemId);
}
