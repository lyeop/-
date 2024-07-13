package com.example.Spring_shop.config;

import com.example.Spring_shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity //웹 보안을 가능하게 한다.
public class SecurityConfig{

    @Autowired
    MemberService memberService;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(auth -> auth
                // CSS, JS, 이미지, 파비콘, 오류 페이지에 대한 요청은 모두 허용합니다.
                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/error").permitAll()
                // 루트 페이지, 회원 관련 페이지, 아이템 페이지, 이미지 페이지에 대한 요청은 모두 허용합니다.
                .requestMatchers("/", "/members/**", "/item/**", "/images/**", "/item/items/**", "/customerCenter/**","/api/**").permitAll()
                // 관리자 페이지에 대한 요청은 ADMIN 역할을 가진 사용자만 허용합니다.
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // 그 외의 모든 요청은 인증된 사용자만 허용합니다.
                .anyRequest().authenticated()
        ).formLogin(formLogin -> formLogin
                // 로그인 페이지 설정
                .loginPage("/members/login")
                // 로그인 성공 시 리다이렉트할 기본 페이지 설정
                .defaultSuccessUrl("/")
                // 로그인 시 사용할 사용자명 파라미터 설정
                .usernameParameter("email")
                // 로그인 실패 시 리다이렉트할 페이지 설정
                .failureUrl("/members/login/error")
        ).logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/")
        ).oauth2Login(oauthLogin -> oauthLogin
                .defaultSuccessUrl("/")
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
        );

        http.exceptionHandling(exception -> exception
                // 인증되지 않은 사용자가 접근할 때 처리할 엔트리 포인트 설정
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        return http.build();
    }
    @Bean
    public static PasswordEncoder passwordEncoder(){
        // 패스워드 인코더 설정
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 사용자 세부 서비스를 설정하고 패스워드 인코더를 적용합니다.
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }
}
