package com.whh.findmuseapi.ios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.whh.findmuseapi.jwt.property.JwtProperties;
import com.whh.findmuseapi.jwt.property.TokenProperty;
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

class JwtServiceTest {
    
    @InjectMocks
    private JwtService jwtService;
    
    @Mock
    private JwtProperties jwtProperties;
    
    @Mock
    private UserRepository userRepository;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock JwtProperties and its inner objects
        TokenProperty refreshProperty = mock(TokenProperty.class);
        TokenProperty accessProperty = mock(TokenProperty.class);
        
        when(refreshProperty.getExpiration()).thenReturn(86400000L);
        when(accessProperty.getExpiration()).thenReturn(3600000L);
        
        when(jwtProperties.getSecretKey()).thenReturn("testSecretKey");
        when(jwtProperties.getRefresh()).thenReturn(refreshProperty);
        when(jwtProperties.getAccess()).thenReturn(accessProperty);
    }
    @Test
    void 리프레시_토큰_생성_테스트() {
        // given
        // setUp에서 이미 설정됨
        
        // when
        String refreshToken = jwtService.createRefreshToken();
        
        // then
        assertNotNull(refreshToken);
        assertTrue(refreshToken.length() > 0);
    }
    
    @Test
    void 액세스_토큰_생성_테스트() {
        // given
        String email = "test@example.com";
        
        // when
        String accessToken = jwtService.createAccessToken(email);
        
        // then
        assertNotNull(accessToken);
        assertTrue(accessToken.length() > 0);
    }
    
    @Test
    void JWT에서_클레임_추출_테스트() {
        // given
        String email = "test@example.com";
        String accessToken = jwtService.createAccessToken(email);
        
        // when
        Optional<String> extractedEmail = jwtService.extractClaimFromJWT(JwtService.CLAIM_EMAIL, accessToken);
        
        // then
        assertTrue(extractedEmail.isPresent());
        assertEquals(email, extractedEmail.get());
    }
    
    @Test
    void 액세스_및_리프레시_토큰_헤더_설정_테스트() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();
        String accessToken = "testAccessToken";
        String refreshToken = "testRefreshToken";
        when(jwtProperties.getAccess().getHeader()).thenReturn("Authorization");
        when(jwtProperties.getRefresh().getHeader()).thenReturn("Refresh-Token");
        
        // when
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        
        // then
        assertEquals(accessToken, response.getHeader("Authorization"));
        assertEquals(refreshToken, response.getHeader("Refresh-Token"));
    }
    
    @Test
    void 리프레시_토큰_업데이트_테스트() throws BadRequestException {
        // given
        String email = "test@example.com";
        String refreshToken = "newRefreshToken";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        
        // when
        jwtService.updateRefreshToken(email, refreshToken);
        
        // then
        verify(userRepository).saveAndFlush(user);
        assertEquals(refreshToken, user.getRefreshToken());
    }
    
    @Test
    void 리프레시_토큰_업데이트_사용자_없음_테스트() {
        // given
        String email = "nonexistent@example.com";
        String refreshToken = "newRefreshToken";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        // when & then
        assertThrows(BadRequestException.class, () -> jwtService.updateRefreshToken(email, refreshToken));
    }
}
