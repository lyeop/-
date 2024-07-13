package com.example.Spring_shop.config;

import com.example.Spring_shop.dto.SessionUser;
import com.example.Spring_shop.entity.Member;
import com.example.Spring_shop.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);

        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 소셜 로그인 제공자 설정
        String provider = getProvider(registrationId);
        attributes.setProvider(provider);

        Member member = saveOrUpdate(attributes);
        httpSession.setAttribute("member", new SessionUser(member));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail());
        if (member != null) {
            member.update(attributes.getName(), attributes.getEmail(), attributes.getPicture(),attributes.getProvider());
        } else {
            member = attributes.toEntity();
        }
        return memberRepository.save(member);
    }

    private String getProvider(String registrationId) {
        // registrationId에 따라 제공자(provider) 정보 반환
        switch (registrationId) {
            case "google":
                return "google";
            case "naver":
                return "naver";
            case "kakao":
                return "kakao";
            default:
                throw new IllegalArgumentException("Unsupported provider: " + registrationId);
        }
    }
}
