package com.mysite.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.PasswordManagementDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration // 스프링의 환경설정 파일
@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만듦. 내부적으로 SpringSecurityFilterChain이 동작하여 URL 필터가 적용
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize 애너테이션을 사용하기 위해 반드시 필요
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers(
                        new AntPathRequestMatcher("/**")).permitAll() // 인증되지 않은 요청을 허락.
                .and()
                .formLogin()
                .loginPage("/user/login") // 로그인 페이지
                .defaultSuccessUrl("/")// 로그인 성공시
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout")) //로그아웃 URL을 /user/logout으로 설정
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true);// 성공시 세션 삭제.

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){ // DB로 해싱함수를 통해 인코딩. Bean으로 등록하여 사용 --> 애플리케이션 곳곳에서 사용.
        return new BCryptPasswordEncoder();
    }
    // AuthenticationManager는 스프링 시큐리티의 인증을 담당한다
    // AuthenticationManager 빈 생성시 스프링의 내부 동작으로 인해 위에서 작성한 UserSecurityService와 PasswordEncoder가 자동으로 설정
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
