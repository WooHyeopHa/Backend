package com.whh.findmuseapi.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.jwt.filter.JwtAuthenticationFilter;
import com.whh.findmuseapi.jwt.filter.JwtExceptionFilter;
import com.whh.findmuseapi.jwt.service.JwtService;
import com.whh.findmuseapi.user.repository.UserRepository;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final UserRepository userRepository;
    // JWT 관련
    private final JwtService jwtService;
    private final JwtExceptionFilter jwtExceptionFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        AuthenticationEntryPoint unauthorizedEntryPoint =
            (request, response, authException) -> {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                String json = mapper.writeValueAsString(ApiResponse.createError(ResponseCode.UNAUTHORIZED_REQUEST‎));
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };
        
        AccessDeniedHandler accessDeniedHandler =
            (request, response, authException) -> {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                String json = mapper.writeValueAsString(ApiResponse.createError(ResponseCode.INVALID_REQUEST‎));
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };
        
        http
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors((corsConfig) ->
                corsConfig.configurationSource(corsConfigurationSource())
            )
            .headers((headersConfig) ->
                headersConfig.frameOptions((frameOptionsConfig -> frameOptionsConfig.disable()))
            )
            // 세션 정책 설정 : 스프링 시큐리티가 생성하지도 않고, 기존 것을 사용하지도 않음
            .sessionManagement((session) ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            //== Unauthorized, Forbidden 처리 ==//
            .exceptionHandling((exceptionConfig) ->
                exceptionConfig
                    .accessDeniedHandler(accessDeniedHandler)
                    .authenticationEntryPoint(unauthorizedEntryPoint)
            )
            // url 접근 설정
            .authorizeHttpRequests(auth ->
                auth
                    // 로그인 요청은 Fillter 검사에서 제외됨
                    .requestMatchers("/oauth/apple/token").permitAll()
                    .requestMatchers("").permitAll()
                    // 그 외 요청은 보안 처리
                    .anyRequest().authenticated()
            );
        
        // 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
        // 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
        // 순서 : LogoutFilter -> jwtAuthenticationProcessingFilter
        http.addFilterAfter(jwtAuthenticationProcessingFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
        
        return http.getOrBuild();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // 허용할 Origin 설정
        configuration.addAllowedMethod("*"); // 허용할 HTTP Method 설정
        configuration.addAllowedHeader("*"); // 허용할 HTTP Header 설정
        configuration.addExposedHeader("Authorization");
        configuration.addExposedHeader("Authorization-refresh");
        configuration.setAllowCredentials(false);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationFilter(jwtService, userRepository);
    }
}

