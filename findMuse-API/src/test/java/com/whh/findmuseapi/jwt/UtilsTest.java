//package com.whh.findmuseapi.jwt;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.whh.findmuseapi.common.constant.ResponseCode;
//import com.whh.findmuseapi.common.util.ApiResponse;
//import com.whh.findmuseapi.jwt.util.Utils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//
//import java.nio.charset.StandardCharsets;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class UtilsTest {
//
//    private MockHttpServletResponse response;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        response = new MockHttpServletResponse();
//        objectMapper = new ObjectMapper();
//    }
//
//    @Test
//    void 에러_응답_정상_전송() throws Exception {
//        // given
//        int status = HttpStatus.UNAUTHORIZED.value();
//        ResponseCode responseCode = ResponseCode.TOKEN_EXPIRED;
//
//        // when
//        Utils.sendErrorResponse(response, status, responseCode);
//
//        // then
//        assertEquals(status, response.getStatus());
//        assertTrue(response.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE));
//        assertEquals(StandardCharsets.UTF_8.name(), response.getCharacterEncoding());
//
//        ApiResponse expectedResponse = ApiResponse.createError(responseCode);
//        ApiResponse actualResponse = objectMapper.readValue(response.getContentAsString(), ApiResponse.class);
//
//        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
//        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
//    }
//}
