package com.whh.findmuseapi.jwt;

import com.whh.findmuseapi.jwt.filter.JwtAuthenticationFilter;
import com.whh.findmuseapi.jwt.service.JwtService;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private FilterChain filterChain;
    
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userRepository);
        SecurityContextHolder.clearContext();
    }
    
    @Test
    void 로그인_요청_필터_무시_테스트() throws ServletException, IOException {
        // given
        when(request.getRequestURI()).thenReturn("/oauth/apple/token");
        
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // then
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    
    @Test
    void 리프레시_토큰_재발급_테스트() throws ServletException, IOException {
        // given
        String refreshToken = "valid.refresh.token";
        String email = "test@example.com";
        when(request.getRequestURI()).thenReturn("/api/some-endpoint");
        when(jwtService.extractRefreshToken(request)).thenReturn(Optional.of(refreshToken));
        when(jwtService.isTokenValid(refreshToken)).thenReturn(true);
        
        User mockUser = mock(User.class);
        when(mockUser.getEmail()).thenReturn(email);
        when(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of(mockUser));
        
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";
        when(jwtService.createAccessToken(mockUser.getEmail())).thenReturn(newAccessToken);
        when(jwtService.createRefreshToken()).thenReturn(newRefreshToken);
        
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // then
        verify(jwtService).sendAccessAndRefreshToken(response, newAccessToken, newRefreshToken);
        verify(userRepository).saveAndFlush(mockUser);
        assertEquals(newRefreshToken, mockUser.getRefreshToken());
    }
    
    @Test
    void 액세스_토큰_인증_성공_테스트() throws ServletException, IOException {
        // given
        String accessToken = "valid.access.token";
        String email = "test@example.com";
        when(request.getRequestURI()).thenReturn("/api/some-endpoint");
        when(jwtService.extractRefreshToken(request)).thenReturn(Optional.empty());
        when(jwtService.extractAccessToken(request)).thenReturn(Optional.of(accessToken));
        when(jwtService.isTokenValid(accessToken)).thenReturn(true);
        when(jwtService.extractClaimFromJWT(JwtService.CLAIM_EMAIL, accessToken)).thenReturn(Optional.of("test@example.com"));
        
        User mockUser = mock(User.class);
        when(mockUser.getEmail()).thenReturn(email);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@example.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }
    
    @Test
    void 액세스_토큰_인증_실패_테스트() throws ServletException, IOException {
        // given
        String accessToken = "invalid.access.token";
        when(request.getRequestURI()).thenReturn("/api/some-endpoint");
        when(jwtService.extractRefreshToken(request)).thenReturn(Optional.empty());
        when(jwtService.extractAccessToken(request)).thenReturn(Optional.of(accessToken));
        when(jwtService.isTokenValid(accessToken)).thenReturn(false);
        
        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        
        // then
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
