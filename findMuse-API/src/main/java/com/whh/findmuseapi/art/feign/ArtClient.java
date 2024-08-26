package com.whh.findmuseapi.art.feign;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import feign.Util;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "artClient", url = "http://kopis.or.kr/openApi/restful/pblprfr", configuration = ArtClient.Configuration.class)
public interface ArtClient {

    // 공연 목록
    @GetMapping()
    Object getArtInfoList(
            @RequestParam(name = "service") String service,
            @RequestParam(name = "stdate") @DateTimeFormat(pattern = "yyyyMMdd")LocalDate startDate,
            @RequestParam(name = "eddate") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate edDate,
            @RequestParam(name = "cpage") String page,
            @RequestParam(name = "rows") String rows,
            @RequestParam(name = "shcate") String genreCode,
            @RequestParam(name = "newsql") String newApiOption);

//    // 공연 상세
//    @PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
//
//
//    //공연 시설 상서
//    @PostMapping(value = "/revoke", consumes = "application/x-www-form-urlencoded")
    class Configuration {
        @Bean
        public Decoder feignDecoder() {
            return (response, type) -> {
                String bodyStr = Util.toString(response.body().asReader(Util.UTF_8));
                JavaType javaType = TypeFactory.defaultInstance().constructType(type);
                return new XmlMapper().readValue(bodyStr, javaType);
            };
        }
    }
}
