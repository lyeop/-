package com.example.Spring_shop.service;

import com.example.Spring_shop.entity.Lizard;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LizardService {
    private static final String LIZARD_URL = "https://xn--699at5i1sh8pu9yi.com/category/%EB%8F%84%EB%A7%88%EB%B1%80/84/";

    public List<Lizard> getLizardData() throws IOException {
        List<Lizard> lizardList = new ArrayList<>();
        Document document = Jsoup.connect(LIZARD_URL).get();
        System.out.println(document);

        // 정확한 CSS 선택자를 사용하여 원하는 요소를 선택합니다.
        Elements contents = document.select("div.anchorBoxId_1196 > div.thumbnail > div.description relative");
        System.out.println(contents);

        for (Element content : contents) {
            Lizard lizard = Lizard.builder()
                    .image(content.select("a img").attr("abs:src")) // 이미지
                    .subject(content.select("p.name a").text())     // 제목
                    .url(content.select("p.name a").attr("abs:href")) // 링크
                    .build();
            lizardList.add(lizard);
            // 데이터 출력 확인
            System.out.println(lizard);
        }
        return lizardList;
    }
}
