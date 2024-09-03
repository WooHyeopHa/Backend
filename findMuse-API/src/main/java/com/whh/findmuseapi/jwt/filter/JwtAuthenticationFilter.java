package com.whh.findmuseapi.jwt.filter;

import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.jwt.service.JwtService;
import com.whh.findmuseapi.jwt.util.Utils;
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
import org.springframework.http.HttpStatus;
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
 * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
 * ---
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는 않는다.
 * 2. RefreshToken이 없고, AccessToken이 유효하지 않거나 없는 경우 -> 인증 실패 처리
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급, 인증은 실패 처리.
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
        
        // 사용자 요청 헤더에서 RefreshToken 추출
        // -> RefreshToken이 없거나 유효하지 않다면(DB에 저장된 RefreshToken과 다르다면 null 반환
        // 사용자의 요청 헤더에 RefreshToken이 있는 경우는, AccessToken이 만료된 경우 밖에 없다.
        // 따라서, 위의 경우를 제외하면 추출한 refreshToken은 모두 null
        String refreshToken = jwtService.extractRefreshToken(request)
            .filter(jwtService::isTokenValid)
            .orElse(null);
        
        
        // RefreshToken이 요청 헤더에 존재한다면, 사용자가 AccessToken이 만료돼서
        // RefreshToken을 보낸 것이므로 리프레시 토큰이 DB의 리프레시 포큰과 일치하는지 확인한다.
        // 만약 일치한다면 AccessToken과 RefreshToken을 재발급해준다.
        if (refreshToken != null) {
            log.info("refresh token 재발급 진행 중...");
            reIssueAccessToken(response, refreshToken);
            return; // 토큰 재발급 후 인증 처리가 진행되지 않도록 막기
        }
        
        // RefreshToken이 없거나 유효하지 않을 때 실행. AccessToken을 검사하고 인증을 처리한는 로직 수행
        // AccessToken이 없거나 유효하지 않음 -> 인증 객체가 담기지 않은 상태로 넘어가기 때문에 403 에러 발생
        // AccessToken 유효 -> 인증 객체가 담긴 상태로 다음 필터로 넘어가기 때문에 인증 성공
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }
    
    /**
     * [리프레시 토큰으로 유저 정보 찾기 & 액세스토큰/리프레시 토큰 재발급 메소드]
     * 파라미터로 들어온 헤더에서 추출한 리프레시 토큰으로 DB에서 유저를 찾고, 해당 유저가 있다면
     * AccessToken 생성 & 리프레시 토큰 재발급 & DB 리프레시 토큰 업데이트
     * 그 후 헤더에 토큰 추가
     * @param response
     * @param refreshToken
     * @throws IOException
     */
    public void reIssueAccessToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken).ifPresent(
            user -> jwtService.sendAccessAndRefreshToken(response,
                jwtService.createAccessToken(user.getEmail()),
                jwtService.reIssueRefreshToken(user))
        );
        log.info("토큰 재발급이 완료되었습니다.");
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
    public void checkAccessTokenAndAuthentication(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        log.info("Access Token 검사 실행");
        
        jwtService.extractAccessToken(request)
            .filter(jwtService::isTokenValid)
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
