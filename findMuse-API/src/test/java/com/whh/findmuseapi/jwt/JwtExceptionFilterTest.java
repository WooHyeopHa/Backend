package com.whh.findmuseapi.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.jwt.filter.JwtExceptionFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JwtExceptionFilterTest {
    
    private JwtExceptionFilter jwtExceptionFilter;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private FilterChain filterChain;
    
    private MockHttpServletResponse response;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtExceptionFilter = new JwtExceptionFilter();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void 토큰_만료_예외_발생시_적절한_응답_반환() throws Exception {
        // given
        TokenExpiredException tokenExpiredException = new TokenExpiredException("Token expired", Instant.now());
        doThrow(tokenExpiredException).when(filterChain).doFilter(request, response);
        
        Method method = JwtExceptionFilter.class.getDeclaredMethod("doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        
        // when
        method.invoke(jwtExceptionFilter, request, response, filterChain);
        
        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
        
        ApiResponse expectedResponse = ApiResponse.createError(ResponseCode.TOKEN_EXPIRED);
        ApiResponse actualResponse = objectMapper.readValue(response.getContentAsString(), ApiResponse.class);
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    }
    
    @Test
    void 정상적인_요청시_필터체인_계속_진행() throws Exception {
        // given
        // 아무것도 설정하지 않음 (정상적인 경우)
        
        Method method = JwtExceptionFilter.class.getDeclaredMethod("doFilterInternal", HttpServletRequest.class, HttpServletResponse.class, FilterChain.class);
        method.setAccessible(true);
        
        // when
        method.invoke(jwtExceptionFilter, request, response, filterChain);
        
        // then
        verify(filterChain).doFilter(request, response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
}
