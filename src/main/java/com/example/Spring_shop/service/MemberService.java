package com.example.Spring_shop.service;

import com.example.Spring_shop.dto.MemberPasswordDto;
import com.example.Spring_shop.dto.MemberUpdateFormDto;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor // final, @NonNull 변수에 붙으면 자동 주입(Autowired)을 해줍니다.
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository; //자동 주입됨
    public Member saveMember(Member member) {
        validateDuplicateMember(member); // 유효성 검사
        return memberRepository.save(member); // 데이터베이스에 저장을 하라는 명령
    }
    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다."); // 예외 발생
        }
        findMember = memberRepository.findByTel(member.getTel());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 전화번호입니다."); // 예외 발생
        }
    }
    public void updateMember(MemberUpdateFormDto memberUpdateFormDto, Long id){

        Member member = memberRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        member.updateMember(memberUpdateFormDto);
    }
    public void updatePassword(MemberPasswordDto memberPasswordDto, PasswordEncoder passwordEncoder){

        Member member = memberRepository.findByEmail(memberPasswordDto.getEmail());

        member.updateMemberPassword(memberPasswordDto,passwordEncoder);

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email);

        if(member == null){
            throw new UsernameNotFoundException(email);
        }
        //빌더패턴
        return User.builder().username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

}
