package com.example.Spring_shop.controller;

import com.example.Spring_shop.dto.*;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.repository.MemberRepository;
import com.example.Spring_shop.service.MailService;
import com.example.Spring_shop.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final HttpSession httpSession;
    private final MemberRepository memberRepository;

    String confirm = "";
    Boolean confirmCheck = false;
    Boolean confirmFindCheck = false;

    @GetMapping(value = "/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "/member/memberForm";
    }

    //----------------------------------------마이 페이지----------------------------------------------------

    @GetMapping(value = "/mypage")
    public String memberMyPage(Model model, Principal principal){

        String email = getEmailFromPrincipalOrSession(principal);
        Member member = memberRepository.findByEmail(email);
        MemberUpdateFormDto memberUpdateFormDto = new MemberUpdateFormDto();

        memberUpdateFormDto.setAddress(member.getAddress());
        memberUpdateFormDto.setTel(member.getTel());

        model.addAttribute("member",member);
        model.addAttribute("memberUpdateFormDto", memberUpdateFormDto);

        return "/member/memberMyPage";
    }


    @PostMapping (value = "/update/{id}")
    public String memberUpdate(@PathVariable Long id, @Valid MemberUpdateFormDto memberUpdateFormDto,
                               BindingResult bindingResult,Model model, Principal principal){

        if (bindingResult.hasErrors()) {
            return "/member/memberMyPage";//다시 회원가입으로 돌려보닙니다.
        }
        try {
            //데이터베이스에 업데이트
            memberService.updateMember(memberUpdateFormDto, id);

        } catch (Exception e){
            model.addAttribute("errorMessage", "정보 수정 중 에러가 발생하였습니다.");
            System.out.println("여기 오류에요");
            return "/member/memberMyPage";
        }

        String email = getEmailFromPrincipalOrSession(principal);
        Member member = memberRepository.findByEmail(email);

        MemberUpdateFormDto memberNewUpdateFormDto = new MemberUpdateFormDto();

        memberNewUpdateFormDto.setAddress(member.getAddress());
        memberNewUpdateFormDto.setTel(member.getTel());

        model.addAttribute("member",member);
        model.addAttribute("memberUpdateFormDto", memberNewUpdateFormDto);

        return "/member/memberMyPage";
    }

    //----------------------------------------------회원가입-------------------------------------------

    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,
                             Model model) {
        // @Valid 붙은 객체를 검사해서 결과에 에러가 있으면 실행
        if (bindingResult.hasErrors()) {
            return "/member/memberForm";//다시 회원가입으로 돌려보닙니다.
        }
        if (!confirmCheck) {
            model.addAttribute("errorMessage", "이메일 인증을 하세요.");
            return "/member/memberForm";
        }
        try {
            //Member 객체 생성
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            //데이터베이스에 저장
            memberService.saveMember(member);
            confirmCheck = false;

        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "/member/memberForm";
        }
        return "redirect:/";
    }

    //--------------------------------------로그인--------------------------------------------------

    @GetMapping(value = "/login")
    public String loginMember(Authentication authentication, Model model) {

        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "/member/memberLoginForm";
    }

    //---------------------------회원가입 이메일 인증 ----------------------------------------------------

    @PostMapping("/{email}/emailConfirm")
    public @ResponseBody ResponseEntity emailConfirm(@PathVariable("email") String email)
            throws Exception {
        confirm = mailService.sendSimpleMessage(email);

        return new ResponseEntity<String>("인증 메일을 보냈습니다.", HttpStatus.OK);
    }

    @PostMapping("/{code}/codeCheck")
    public @ResponseBody ResponseEntity codeConfirm(@PathVariable("code") String code, Model model)
            throws Exception {

        if (confirm.equals(code)) {
            confirmCheck = true;
            return new ResponseEntity<String>("인증 성공", HttpStatus.OK);

        } else {
            return new ResponseEntity<String>("인증 코드를 올바르게 입력해주세요.", HttpStatus.BAD_REQUEST);
        }
    }

    // --------------------------------------------------비밀번호 찾기 ------------------------------------------

    @GetMapping(value = "/findpassword")
    public String memberFindPassword(Model model){

        MemberEmailDto memberEmailDto = new MemberEmailDto();

        model.addAttribute("memberEmailDto",memberEmailDto);

        return "/member/memberFindPassword";

    }


    @PostMapping(value = "/FindPassword2")
    public String memberFindPassword2(@Valid MemberEmailDto memberEmailDto, BindingResult bindingResult,
                                      Model model) {

        //유효성 검사
        if (bindingResult.hasErrors()) {

            MemberEmailDto memberEmailDto1=new MemberEmailDto();
            model.addAttribute("memberEmailDto",memberEmailDto1);

            return "/member/memberFindPassword";
        }
        //인증이 안되어있으면 다시 돌려보내기
        if (!confirmFindCheck) {
            model.addAttribute("errorMessage", "이메일 인증을 하세요.");
            MemberEmailDto memberEmailDto1=new MemberEmailDto();
            model.addAttribute("memberEmailDto",memberEmailDto1);
            return "/member/memberFindPassword";
        }
        Member member = memberRepository.findByEmail(memberEmailDto.getEmail());
        MemberPasswordDto memberPasswordDto = new MemberPasswordDto();
        memberPasswordDto.setEmail(member.getEmail());

        model.addAttribute("member",member);
        model.addAttribute("memberPasswordDto",memberPasswordDto);

        return "/member/memberFindPassword2";
    }

    @PostMapping(value = "/memberUpdatePassword")
    public String memberUpdatePassword1(@Valid MemberPasswordDto memberPasswordDto, BindingResult bindingResult,
                                        Model model) {

        //유효성 검사
        if (bindingResult.hasErrors()) {

            MemberPasswordDto memberPasswordDto1=new MemberPasswordDto();
            model.addAttribute("memberPasswordDto",memberPasswordDto1);

            return "/member/memberFindPassword2";
        }

        memberService.updatePassword(memberPasswordDto, passwordEncoder);

        return "/member/memberLoginForm";
    }

    @PostMapping("/{email}/emailFindConfirm")
    public @ResponseBody ResponseEntity emailFindConfirm(@PathVariable("email") String email)
            throws Exception {

        confirm = mailService.sendSimpleMessage(email);

        return new ResponseEntity<String>("인증 메일을 보냈습니다.", HttpStatus.OK);
    }


    @PostMapping("/{code}/codeFindCheck")
    public @ResponseBody ResponseEntity codeFindConfirm(@PathVariable("code") String code, Model model)
            throws Exception {

        if (confirm.equals(code)) {

            confirmFindCheck = true;

            return new ResponseEntity<String>("인증 성공", HttpStatus.OK);

        } else {
            return new ResponseEntity<String>("인증 코드를 올바르게 입력해주세요.", HttpStatus.BAD_REQUEST);
        }
    }

    //---------------------------------------------로그인 정보 찾기 ---------------------------------------------

    private String getEmailFromPrincipalOrSession(Principal principal) {
        SessionUser user = (SessionUser) httpSession.getAttribute("member");
        if (user != null) {
            return user.getEmail();
        }
        return principal.getName();
    }
}
