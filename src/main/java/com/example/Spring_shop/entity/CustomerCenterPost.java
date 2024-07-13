package com.example.Spring_shop.entity;


import com.example.Spring_shop.constant.Role;
import com.example.Spring_shop.dto.CustomerCenterPostFormDto;
import com.example.Spring_shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
@Table(name="customerCenterPost")
public class CustomerCenterPost {

    @Id
    @Column(name="customerCenterPost_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writer;
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "member_customerCenterPost",
            joinColumns = @JoinColumn(name = "customerCenterPost_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Member member;

    /*
    public boolean canDelete(Authentication authentication) {
        // 현재 로그인한 사용자의 정보 가져오기
        String currentUsername = authentication.getName();

        // 게시글의 작성자와 현재 사용자의 이름 비교
        return this.member != null && this.member.getEmail().equals(currentUsername);
    }

     */


    public CustomerCenterPost() {
        // 필드 초기화 또는 아무 작업도 하지 않아도 됩니다.
    }

    public void updateCustomerCenterPost(CustomerCenterPostFormDto customerCenterPostFormDto){
        this.writer = customerCenterPostFormDto.getWriter();
        this.title = customerCenterPostFormDto.getTitle();
        this.content = customerCenterPostFormDto.getContent();

    }


    public CustomerCenterPost(String writer, String title, String content) {
        this.writer = writer;
        this.title = title;
        this.content = content;
    }

    public static CustomerCenterPost createCustomerCenterPost(CustomerCenterPostFormDto customerCenterPostFormDto){

        CustomerCenterPost customerCenterPost = new CustomerCenterPost(customerCenterPostFormDto.getWriter(),
                customerCenterPostFormDto.getTitle(),
                customerCenterPostFormDto.getContent());

        customerCenterPost.setWriter(customerCenterPostFormDto.getWriter());
        customerCenterPost.setTitle(customerCenterPostFormDto.getTitle());
        customerCenterPost.setContent(customerCenterPostFormDto.getContent());

        return customerCenterPost;
    }
}