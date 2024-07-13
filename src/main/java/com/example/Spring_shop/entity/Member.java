package com.example.Spring_shop.entity;

import com.example.Spring_shop.constant.Role;
import com.example.Spring_shop.dto.MemberFormDto;
import com.example.Spring_shop.dto.MemberPasswordDto;
import com.example.Spring_shop.dto.MemberUpdateFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity // 나 엔티티야
@Table(name = "member") // 테이블 명
@Getter
@Setter
@ToString
public class Member extends BaseEntity{
    //기본키 컬럼명 = member_id AI-> 데이터 저장시 1씩 증가
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //알아서 설정
    private String name;
    // 중복 허용 X
    @Column(unique = true)
    private String email;
    //알아서
    private String password;
    //알아서
    private String address;
    private String provider;
    private String picture;

    private String tel;
    // Cart와의 관계 설정
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
    private Cart cart;

    // 기본 생성자 (JPA 규약을 따라야 함)
    public Member() {
    }

    public void updateMember(MemberUpdateFormDto memberUpdateFormDto){
        this.address = memberUpdateFormDto.getAddress();
        this.tel = memberUpdateFormDto.getTel();
    }

    public void updateMemberPassword(MemberPasswordDto memberPasswordDto, PasswordEncoder passwordEncoder){
        String Newpassword = passwordEncoder.encode(memberPasswordDto.getPassword());

        this.password = Newpassword;
    }


    public Member(String name, String email, String picture,Role role,String provider, String tel, String address) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role=role;
        this.provider = provider;
        this.tel = "not exist";
        this.address = "not exist";

    }
    public Member update(String name, String email, String picture, String provider) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
        return this;
    }
    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        member.setTel(memberFormDto.getTel());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return member;
    }

    //Enum -> 컴 : 숫자 우리 : 문자
    //데이터베이스 문자 그대로 -> USER, ADMIN
    @Enumerated(EnumType.STRING)
    private Role role;
}