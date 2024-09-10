package com.whh.findmuseapi.art.feign;

import com.whh.findmuseapi.art.openApi.dto.ArtInfoDetailResponse;
import com.whh.findmuseapi.art.openApi.dto.ArtInfoResponse;
import com.whh.findmuseapi.art.openApi.dto.PlaceInfoDetailResponse;
import feign.codec.Decoder;
import feign.jaxb.JAXBContextFactory;
import feign.jaxb.JAXBDecoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "artClient", url = "http://kopis.or.kr/openApi/restful" ,configuration = ArtClient.Configuration.class)
public interface ArtClient {

    // 공연ID 목록 API
    @GetMapping("/pblprfr")
    ArtInfoResponse getArtInfoList(
            @RequestParam(name = "service") String service,
            @RequestParam(name = "stdate") @DateTimeFormat(pattern = "yyyyMMdd")LocalDate startDate,
            @RequestParam(name = "eddate") @DateTimeFormat(pattern = "yyyyMMdd") LocalDate edDate,
            @RequestParam(name = "cpage") String page,
            @RequestParam(name = "rows") String rows,
            @RequestParam(name = "shcate") String genreCode,
            @RequestParam(name = "newsql") String newApiOption);

    // 공연 상세 API
    @GetMapping("/pblprfr/{mt20id}")
    ArtInfoDetailResponse getArtInfoDetail(
            @PathVariable(name = "mt20id") String artId,
            @RequestParam(name = "service") String service,
            @RequestParam(name = "newsql") String newApiOption);


    //공연 시설 상세 API
    @GetMapping("/prfplc/{mt10id}")
    PlaceInfoDetailResponse getPlaceInfoDetail(
            @PathVariable(name = "mt10id") String placeId,
            @RequestParam(name = "service") String service,
            @RequestParam(name = "newsql") String newApiOption);


    /**
     * XML를 위한 디코더
     */
    class Configuration {
        @Bean
        public Decoder xmlDecoder() {
            return new JAXBDecoder(new JAXBContextFactory.Builder()
                    .withMarshallerJAXBEncoding("UTF-8")
                    .build());
        }
    }
}
