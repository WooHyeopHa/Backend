package com.whh.findmuseapi.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.whh.findmuseapi.common.exception.CustomBadRequestException;
import com.whh.findmuseapi.jwt.property.JwtProperties;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@RequiredArgsConstructor
@EnableConfigurationProperties({ JwtProperties.class })
@Service
public class JwtService {
    
    private final JwtProperties jwtProperties;
    
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    
    public static final String CLAIM_EMAIL = "email";
    
    private static final String BEARER = "Bearer ";
    
    private final UserRepository userRepository;
    
    /**
     * RefreshToken 생성
     * RefreshToken은 Claim을 넣지 않으므로 withClaim() X
     */
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
            .withSubject(REFRESH_TOKEN_SUBJECT)
            .withExpiresAt(new Date(now.getTime() + jwtProperties.getRefresh().getExpiration() * 1000))
            .sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));
    }
    
    /**
     * AccessToken 생성 메소드
     */
    public String createAccessToken(String email) {
        Date now = new Date();
        return JWT.create()
            .withSubject(ACCESS_TOKEN_SUBJECT)
            .withExpiresAt(new Date(now.getTime() + jwtProperties.getAccess().getExpiration() * 1000))
            
            // claim으로 email 사용
            .withClaim(CLAIM_EMAIL, email)
            .sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));
    }
    
    /**
     * AccessToken에서 claim 추출
     * 추출 전에 JWT.require()로 검증기 생성
     * verify로 AceessToken 검증 후 유효하다면 추출
     * 유효하지 않다면 빈 Optional 객체 반환
     */
    public Optional<String> extractClaimFromJWT(String claim, String accessToken) {
        try {
            // 토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier builder 변환
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey()))
                .build() // 변환된 빌더로 JWT verifier 생성
                .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생
                .getClaim(claim)
                .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다. : " + e);
            return Optional.empty();
        }
    }
    
    /**
     * AccessToken + RefreshToken 헤더에 실어서 보내기
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        
        response.setHeader(jwtProperties.getAccess().getHeader(), accessToken);
        response.setHeader(jwtProperties.getRefresh().getHeader(), refreshToken);
        
        log.info("Headers : " + response.getHeaderNames().toString());
        log.info("AccessToken 설정 완료 : {}", accessToken);
        log.info("RefreshToken 설정 완료 : {}", refreshToken);
    }
    
    /**
     * RefreshToken DB 저장(업데이트)
     */
    public void updateRefreshToken(String email, String refreshToken) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            user.updateRefreshToken(refreshToken);
            userRepository.saveAndFlush(user);
        } else {
            log.info(email + "로 가입된 유저가 없습니다.");
            throw new CustomBadRequestException(email);
        }
    }
    
    /**
     * 헤더에서 RefreshToken 추출
     * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토크만 가져오기 위해서
     * 헤더를 가져온 후 "Bearer "를 삭제(""로 replace)
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getRefresh().getHeader()))
            .filter(refreshToken -> refreshToken.startsWith(BEARER))
            .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }
    
    /**
     * 헤더에서 AccessToken 추출
     * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토크만 가져오기 위해서
     * 헤더를 가져온 후 "Bearer "를 삭제(""로 replace)
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(jwtProperties.getAccess().getHeader()))
            .filter(accessToken -> accessToken.startsWith(BEARER))
            .map(accessToken -> accessToken.replace(BEARER, ""));
    }
    
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(jwtProperties.getSecretKey())).build().verify(token);
            return true;
        } catch (TokenExpiredException e) {
            log.info("유효기간이 만료된 토큰입니다. {} {}", e.getMessage(), e.getExpiredOn());
            throw new TokenExpiredException(e.getMessage(), e.getExpiredOn());
        } catch (Exception e) {
            log.info("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
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
                user -> sendAccessAndRefreshToken(response,
                        createAccessToken(user.getEmail()),
                        refreshToken)
        );
        log.info("AccessToken 재발급이 완료되었습니다.");
    }

    /**
     * [리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드]
     * @param user
     * @return reIssuedRefreshToken
     */
    public String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }
}
