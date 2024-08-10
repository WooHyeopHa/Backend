package com.whh.findmuseapi.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            // 세션 설정
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // h2 database 접근을 위한 프레임 허용
            .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            // url 접근 설정
            .authorizeHttpRequests(auth ->
                auth
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/users/me").authenticated()
                    .requestMatchers("/users/**").permitAll()
                    .anyRequest().permitAll()
            )
//            // oauth 설정
//            .oauth2Login(oauth2 ->
//                oauth2.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
//                    .successHandler(oAuth2AuthenticationSuccessHandler)
//                    .permitAll()
//            )
//            // jwt 토큰 설정
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//            .exceptionHandling(exception ->
//                exception.authenticationEntryPoint(customAuthenticationEntryPoint)
//                    .accessDeniedHandler(customAccessDeniedHandler)
//            )
            .build();
    }
}

