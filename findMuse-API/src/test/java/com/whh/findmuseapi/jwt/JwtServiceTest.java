//package com.whh.findmuseapi.jwt;
//
//import com.whh.findmuseapi.jwt.property.TokenProperty;
//import com.whh.findmuseapi.jwt.service.JwtService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import com.auth0.jwt.JWT;
//import com.auth0.jwt.algorithms.Algorithm;
//import com.whh.findmuseapi.jwt.property.JwtProperties;
//import com.whh.findmuseapi.user.entity.User;
//import com.whh.findmuseapi.user.repository.UserRepository;
//
//import java.util.Date;
//import java.util.Optional;
//
//class JwtServiceTest {
//    @Mock
//    private JwtProperties jwtProperties;
//
//    @Mock
//    private TokenProperty refreshTokenProperty;
//
//    @Mock
//    private TokenProperty accessTokenProperty;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private JwtService jwtService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Mock 객체 설정
//        when(jwtProperties.getSecretKey()).thenReturn("testSecretKey");
//
//        // Mocked refresh and access properties
//        when(jwtProperties.getRefresh()).thenReturn(refreshTokenProperty);
//        when(jwtProperties.getAccess()).thenReturn(accessTokenProperty);
//
//        // Refresh token property 설정
//        when(refreshTokenProperty.getExpiration()).thenReturn(604800L); // 7 days
//        when(refreshTokenProperty.getHeader()).thenReturn("X-Refresh-Token");
//
//        // Access token property 설정
//        when(accessTokenProperty.getExpiration()).thenReturn(3600L); // 1 hour
//        when(accessTokenProperty.getHeader()).thenReturn("Authorization");
//    }
//
//
//    @Test
//    void RefreshToken_생성_테스트() {
//        // given
//        // setUp에서 이미 주어진 상태
//
//        // when
//        String refreshToken = jwtService.createRefreshToken();
//
//        // then
//        assertNotNull(refreshToken);
//        assertTrue(jwtService.isTokenValid(refreshToken));
//    }
//
//    @Test
//    void AccessToken_생성_테스트() {
//        // given
//        String email = "test@example.com";
//
//        // when
//        String accessToken = jwtService.createAccessToken(email);
//
//        // then
//        assertNotNull(accessToken);
//        assertTrue(jwtService.isTokenValid(accessToken));
//        assertEquals(email, jwtService.extractClaimFromJWT(JwtService.CLAIM_EMAIL, accessToken).orElse(null));
//    }
//
//    @Test
//    void JWT에서_Claim_추출_테스트() {
//        // given
//        String email = "test@example.com";
//        String accessToken = jwtService.createAccessToken(email);
//
//        // when
//        Optional<String> extractedEmail = jwtService.extractClaimFromJWT(JwtService.CLAIM_EMAIL, accessToken);
//
//        // then
//        assertTrue(extractedEmail.isPresent());
//        assertEquals(email, extractedEmail.get());
//    }
//
//    @Test
//    void 응답에_AccessToken과_RefreshToken_설정_테스트() {
//        // given
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        String accessToken = "testAccessToken";
//        String refreshToken = "testRefreshToken";
//
//        // when
//        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
//
//        // then
//        assertEquals(accessToken, response.getHeader(jwtProperties.getAccess().getHeader()));
//        assertEquals(refreshToken, response.getHeader(jwtProperties.getRefresh().getHeader()));
//    }
//
//    @Test
//    void 요청에서_RefreshToken_추출_테스트() {
//        // given
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        String refreshToken = "testRefreshToken";
//        request.addHeader(jwtProperties.getRefresh().getHeader(), "Bearer " + refreshToken);
//
//        // when
//        Optional<String> extractedToken = jwtService.extractRefreshToken(request);
//
//        // then
//        assertTrue(extractedToken.isPresent());
//        assertEquals(refreshToken, extractedToken.get());
//    }
//
//    @Test
//    void 요청에서_AccessToken_추출_테스트() {
//        // given
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        String accessToken = "testAccessToken";
//        request.addHeader(jwtProperties.getAccess().getHeader(), "Bearer " + accessToken);
//
//        // when
//        Optional<String> extractedToken = jwtService.extractAccessToken(request);
//
//        // then
//        assertTrue(extractedToken.isPresent());
//        assertEquals(accessToken, extractedToken.get());
//    }
//
//    @Test
//    void 토큰_유효성_검증_테스트() {
//        // given
//        String validToken = JWT.create()
//            .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
//            .sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));
//
//        String expiredToken = JWT.create()
//            .withExpiresAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60))
//            .sign(Algorithm.HMAC512(jwtProperties.getSecretKey()));
//
//        // when & then
//        assertTrue(jwtService.isTokenValid(validToken));
//        assertThrows(com.auth0.jwt.exceptions.TokenExpiredException.class, () -> jwtService.isTokenValid(expiredToken));
//    }
//}