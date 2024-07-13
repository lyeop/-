package com.example.Spring_shop.controller;


import com.example.Spring_shop.dto.CustomerCenterPostDto;
import com.example.Spring_shop.dto.CustomerCenterPostFormDto;
import com.example.Spring_shop.dto.MemberFormDto;
import com.example.Spring_shop.dto.SessionUser;
import com.example.Spring_shop.entity.CustomerCenterPost;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.repository.CustomerCenterRepository;
import com.example.Spring_shop.repository.MemberRepository;
import com.example.Spring_shop.service.CustomerCenterService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerCenterController {

    @Autowired
    private CustomerCenterRepository customerCenterPostRepository;
    @Autowired
    private CustomerCenterService customerCenterService;
    @Autowired
    private MemberRepository memberRepository;

    private final HttpSession httpSession;


    @GetMapping("/customerCenter")
    public String customerCenter(Model model) {
        // 데이터베이스에서 모든 게시물을 가져옴
        List<CustomerCenterPost> posts = customerCenterPostRepository.findAll();

        // Entity를 DTO로 변환하여 모델에 추가
        List<CustomerCenterPostDto> dtos = new ArrayList<>();
        for (CustomerCenterPost post : posts) {
            dtos.add(new CustomerCenterPostDto(post.getId(),post.getWriter(),post.getTitle(),post.getContent()));
        }
        model.addAttribute("dtos", dtos);

        return "/customerCenter/customerMain";
    }


    @GetMapping("/customerCenter/write")
    public String customerCenterPost(Model model) {

        model.addAttribute("customerCenterPostFormDto", new CustomerCenterPostFormDto());

        return "/customerCenter/customerForm";
    }

    @PostMapping(value = "/customerCenter/new")
    public String customerCenterPostForm(@Valid CustomerCenterPostFormDto customerCenterPostFormDto, BindingResult bindingResult,
                                         Model model, Principal principal) {
        // @Valid 붙은 객체를 검사해서 결과에 에러가 있으면 실행
        if (bindingResult.hasErrors()) {
            return "/customerCenter/customerForm";//다시 작성화면으로 돌려보냄
        }
        try {
            //데이터베이스에 저장
            String email = getEmailFromPrincipalOrSession(principal);

            customerCenterService.CreateCustomerCenterPost(customerCenterPostFormDto, email);

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/customerCenter/customerForm";
        }
        List<CustomerCenterPost> posts = customerCenterPostRepository.findAll();

        // Entity를 DTO로 변환하여 모델에 추가
        List<CustomerCenterPostDto> dtos = new ArrayList<>();
        for (CustomerCenterPost post : posts) {
            dtos.add(new CustomerCenterPostDto(post.getId(),post.getWriter(),post.getTitle(),post.getContent()));
        }
        model.addAttribute("dtos", dtos);
        return "/customerCenter/customerMain";
    }

    @GetMapping("/view/{id}")
    public String viewCustomerCenterPost(@PathVariable Long id, Model model, Principal principal) {
        // id를 이용하여 글 상세 정보를 조회합니다.

        CustomerCenterPost post = customerCenterService.getCustomerCenterPostById(id);

        String email = getEmailFromPrincipalOrSession(principal);

        //현재 로그인 된 멤버
        Member member = memberRepository.findByEmail(email);
        // 모델에 글 정보를 추가합니다.
        model.addAttribute("post", post);
        model.addAttribute("member", member);

        return "/customerCenter/customerPostView"; // 상세 정보를 보여줄 뷰 페이지로 이동합니다.
    }

    @PostMapping("/customerCenter/delete/{id}")
    public String deleteCustomerCenterPost(@PathVariable Long id,Model model) {
        customerCenterService.deleteCustomerCenterPostById(id);

        List<CustomerCenterPost> posts = customerCenterPostRepository.findAll();

        List<CustomerCenterPostDto> dtos = new ArrayList<>();
        for (CustomerCenterPost post : posts) {
            dtos.add(new CustomerCenterPostDto(post.getId(),post.getWriter(),post.getTitle(),post.getContent()));
        }
        model.addAttribute("dtos", dtos);

        return "/customerCenter/customerMain";
    }


    @PostMapping("/customerCenter/update/{id}")
    public String updateCustomerCenterPost(@PathVariable Long id,Model model,
                                           Principal principal) {

        // id를 이용하여 글 상세 정보를 조회합니다.
        CustomerCenterPostFormDto customerCenterPostFormDto = customerCenterService.getCustomerCenterPost(id);
        model.addAttribute("customerCenterPostFormDto", customerCenterPostFormDto);

        return "/customerCenter/customerUpdateForm";
    }


    @PostMapping("/customerCenter/updateSave/{id}")
    public String updateSaveCustomerCenterPost(@PathVariable Long id, @Valid CustomerCenterPostFormDto customerCenterPostFormDto,
                                               Model model, BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            System.out.println("오류났어요");
            return "/customerCenter/customerUpdateForm";//다시 작성화면으로 돌려보냄
        }
        try {
            //데이터베이스에 업데이트
            customerCenterService.updateCustomerCenterPost(customerCenterPostFormDto);

        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            System.out.println("여기 오류에요");
            return "/customerCenter/customerUpdateForm";
        }

        List<CustomerCenterPost> posts = customerCenterPostRepository.findAll();
        // Entity를 DTO로 변환하여 모델에 추가

        List<CustomerCenterPostDto> dtos = new ArrayList<>();
        for (CustomerCenterPost post : posts) {
            dtos.add(new CustomerCenterPostDto(post.getId(),post.getWriter(),post.getTitle(),post.getContent()));
        }
        model.addAttribute("dtos", dtos);

        return "/customerCenter/customerMain";
    }
    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }
}
