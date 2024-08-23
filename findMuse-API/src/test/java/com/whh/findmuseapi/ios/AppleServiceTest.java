package com.whh.findmuseapi.ios;

import com.whh.findmuseapi.common.constant.Infos.Role;
import com.whh.findmuseapi.ios.config.AppleProperties;
import com.whh.findmuseapi.ios.dto.AppleLoginResponse;
import com.whh.findmuseapi.ios.dto.AppleToken;
import com.whh.findmuseapi.ios.feign.AppleAuthClient;
import com.whh.findmuseapi.ios.service.AppleService;
import com.whh.findmuseapi.ios.util.AppleJwtUtils;
import com.whh.findmuseapi.jwt.service.JwtService;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import java.util.Optional;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AppleServiceTest {
    
    @InjectMocks
    private AppleService appleService;
    
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private AppleProperties appleProperties;
    @Mock
    private AppleAuthClient appleAuthClient;
    @Mock
    private AppleJwtUtils appleJwtUtils;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
//    @Test
//    void 애플_로그인_성공_신규_사용자() throws BadRequestException {
//        // given
//        AppleLoginResponse appleLoginResponse = AppleLoginResponse.builder()
//            .state("state")
//            .code("code")
//            .idToken("idToken")
//            .build();
//
//        AppleToken.Response tokenResponse = new AppleToken.Response();
//        tokenResponse.setAccess_token("accessToken");
//        tokenResponse.setId_token("refreshToken");
//        tokenResponse.setRefresh_token("refreshToken");
//        tokenResponse.setExpires_in("3600L");
//
//        User newUser = User.builder()
//            .accountId("accountId")
//            .email("email@example.com")
//            .role(Role.GUEST)
//            .accessToken("accessToken")
//            .refreshToken("refreshToken")
//            .build();
//
//        when(appleJwtUtils.verifyIdentityToken(anyString())).thenReturn(true);
//        when(appleAuthClient.getToken(any())).thenReturn(tokenResponse);
//        when(userRepository.findByAccountId(anyString())).thenReturn(Optional.empty());
//        when(userRepository.save(any(User.class))).thenReturn(newUser);
//
//        String clientId = "";
//        String keyPath = "";  // 임시로 사용할 경로
//        String loginKey = "";
//        String teamId = "";
//        when(appleProperties.getClientId()).thenReturn(clientId);
//        when(appleProperties.getKeyPath()).thenReturn(keyPath);
//        when(appleProperties.getLoginKey()).thenReturn(loginKey);
//        when(appleProperties.getTeamId()).thenReturn(teamId);
//
//        // when
//        User result = appleService.login(appleLoginResponse);
//
//        // then
//        assertNotNull(result);
//        assertEquals("accountId", result.getAccountId());
//        assertEquals("email@example.com", result.getEmail());
//        verify(userRepository).save(any(User.class));
//
//    }
//
//    private String generateValidJwt(String clientId) throws Exception {
//        PrivateKey privateKey = appleService.getPrivateKey(); // 통과 후 priavte로 변경
//
//        JWTClaimsSet claimsSet = new JWTClaimsSet();
//        claimsSet.setIssuer("https://appleid.apple.com");
//        claimsSet.setAudience(clientId);
//        claimsSet.setExpirationTime(new Date(System.currentTimeMillis() + 1000 * 60));
//
//        SignedJWT signedJWT = new SignedJWT(
//            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("123").build(),
//            claimsSet);
//
//        JWSSigner signer = new RSASSASigner(privateKey);
//        signedJWT.sign(signer);
//
//        return signedJWT.serialize();
//    }
    @Test
    void 애플_로그인_성공_기존_사용자() throws BadRequestException {
        // given
        AppleLoginResponse appleLoginResponse = AppleLoginResponse.builder()
            .state("state")
            .code("code")
            .idToken("idToken")
            .build();
        
        AppleToken.Response tokenResponse = new AppleToken.Response();
        tokenResponse.setAccess_token("accessToken");
        tokenResponse.setId_token("refreshToken");
        tokenResponse.setRefresh_token("refreshToken");
        tokenResponse.setExpires_in("3600L");
        
        User existingUser = User.builder()
            .accountId("accountId")
            .email("email@example.com")
            .role(Role.GUEST)
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .build();

        when(appleJwtUtils.verifyIdentityToken(anyString())).thenReturn(true);
        when(appleAuthClient.getToken(any())).thenReturn(tokenResponse);
        when(userRepository.findByAccountId(anyString())).thenReturn(Optional.of(existingUser));
        
        // when
        User result = appleService.login(appleLoginResponse);
        
        // then
        assertNotNull(result);
        assertEquals("accountId", result.getAccountId());
        assertEquals("newAccessToken", result.getAccessToken());
        verify(userRepository).save(existingUser);
    }
    
    @Test
    void 애플_계정_삭제_성공() throws BadRequestException {
        // given
        String accessToken = "validAccessToken";
        User user = User.builder()
            .accountId("accountId")
            .email("email@example.com")
            .role(Role.GUEST)
            .accessToken(accessToken)
            .refreshToken("refreshToken")
            .build();
        
        
        when(jwtService.extractClaimFromJWT(eq(JwtService.CLAIM_EMAIL), anyString())).thenReturn(Optional.of("email@example.com"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));
        
        // when
        appleService.deleteAppleAccount(accessToken);
        
        // then
        verify(userRepository).delete(user);
    }
    
    @Test
    void 로그인_성공_후_토큰_발급() throws BadRequestException {
        // given
        User user = User.builder()
            .accountId("accountId")
            .email("email@example.com")
            .role(Role.GUEST)
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .build();
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";
        
        when(jwtService.createAccessToken(anyString())).thenReturn(newAccessToken);
        when(jwtService.createRefreshToken()).thenReturn(newRefreshToken);
        
        // when
        appleService.loginSuccess(user, response);
        
        // then
        verify(jwtService).sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
        verify(jwtService).updateRefreshToken("email@example.com", newRefreshToken);
    }
    
    @Test
    void 잘못된_아이디토큰으로_로그인_실패() throws BadRequestException{
        // given
        AppleLoginResponse appleLoginResponse = AppleLoginResponse.builder()
            .state("state")
            .code("code")
            .idToken("invalidIdToken")
            .build();
        
        when(appleJwtUtils.verifyIdentityToken(anyString())).thenReturn(false);
        
        // when & then
        assertThrows(BadRequestException.class, () -> appleService.login(appleLoginResponse));
    }
}