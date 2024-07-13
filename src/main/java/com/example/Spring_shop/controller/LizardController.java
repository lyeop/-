package com.example.Spring_shop.controller;

import com.example.Spring_shop.entity.Lizard;
import com.example.Spring_shop.service.LizardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

@Controller
public class LizardController {
    private final LizardService lizardService;

    public LizardController(LizardService lizardService) {
        this.lizardService = lizardService;
    }

    @GetMapping("/item/lizard")
    public String lizard(Model model) {
        System.out.println("aaaa");
        List<Lizard> lizardList = null;
        try {
            lizardList = lizardService.getLizardData();
        } catch (IOException e) {
            e.printStackTrace(); // 예외 처리 필요
        }
        model.addAttribute("lizardList", lizardList);
        return "item/lizard"; // Thymeleaf 템플릿 경로 반환
    }
}

