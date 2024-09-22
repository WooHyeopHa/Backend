package com.whh.findmuseapi.jwt.filter;

import com.whh.findmuseapi.jwt.service.JwtService;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import static com.whh.findmuseapi.jwt.service.JwtService.CLAIM_EMAIL;

/**
 * Jwt 인증 필터
 * ---
 * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
 * AccessToken 만료 시에만 RefreshToken을 담아서 요청
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserRepository userRepository;
    
    @Override
    public void doFilterInternal(HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        String refreshToken = jwtService.extractRefreshToken(request)
            .filter(jwtService::isTokenValid)
            .orElse(null);

        // AceessToken이 유효하면, 인증 진행
        if (accessToken != null) {
            AuthenticateUser(request, response, filterChain);
        }

        // RefreshToken이 유효하면, AccessToken 재발급
        if (refreshToken != null) {
            log.info("access token 재발급 진행 중...");
            jwtService.reIssueAccessToken(response, refreshToken);
        }

        filterChain.doFilter(request, response);
    }
    
    /**
     * [액세스 토큰 체크 & 인증 처리 메소드]
     * request에서 액세스 토큰 추출 후, 유효한 토큰인지 검증
     * 유효한 토큰이면, 액세스 토큰에서 email 추출한 후 유저 객체 찾기
     * 그 유저 객체를 인증 처리한 후, SecurityContextHolder에 담기
     * 그 후 다음 인증 필터로 진행
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    public void AuthenticateUser(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        
        jwtService.extractAccessToken(request)
            .flatMap(accessToken -> jwtService.extractClaimFromJWT(CLAIM_EMAIL, accessToken))
            .flatMap(userRepository::findByEmail)
            .ifPresentOrElse(user -> {
                saveAuthentication(user);
                log.info("JWT 인증 성공");
            }, () -> log.info("JWT 인증 실패 : 존재하지 않는 회원"));
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * [인증 허가 메소드]
     * param의 유저 : 우리가 만든 객체 / builder의 유저 : UserDetails의 user 객체
     * ---
     * UserDetails의 user 객체 안에 Set<GrantedAuthority> authorites이 있어서 getter로 호출 후
     * authoritiesMapper로 매핑하기
     * ---
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * 미리 만들어둔 Authentication 객체에 대한 인증 허가 처리
     * @param user
     */
    public void saveAuthentication(User user) {
        GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
        
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .roles(user.getRole().getKey())
            .build();
        
        // UserDetails의 user 객체 안에 Set<GrantedAuthority> authorites이 있어서 getter로 호출 후
        // authoritiesMapper로 매핑하기
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
            authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
