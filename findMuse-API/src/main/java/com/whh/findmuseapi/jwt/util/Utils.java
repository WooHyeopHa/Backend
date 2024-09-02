package com.whh.findmuseapi.jwt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

@Slf4j
public class Utils {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static void sendErrorResponse(HttpServletResponse response, int status, ResponseCode responseCode) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(status);
        
        response.getWriter().write(mapper.writeValueAsString(ApiResponse.createError(responseCode)));
    }
}
