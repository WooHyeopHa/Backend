package com.whh.findmuseapi.art.openApi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whh.findmuseapi.art.feign.ArtClient;
import com.whh.findmuseapi.art.openApi.ArtApiProperties;
import com.whh.findmuseapi.art.openApi.dto.ArtInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@EnableConfigurationProperties(ArtApiProperties.class)
@Slf4j
@Service
public class ArtOpenApiService {

    private final ArtClient artClient;
    private final ObjectMapper ob;
    private final ArtApiProperties artProperties;

    //공연 목록 가져오기
    public void getOpenApiArtList() {
        //1. 현재날짜 불러와서 -> String으로 변환
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.of(2024, 12, 31);

        List<String> musicalIds = getIdList(fromDate, toDate, artProperties.getMusical());
        List<String> darmaIds = getIdList(fromDate, toDate, artProperties.getDrama());
        List<String> danceIds = getIdList(fromDate, toDate, artProperties.getDance());
        danceIds.addAll(getIdList(fromDate, toDate, artProperties.getDanceBasic()));
        List<String> classicIds = getIdList(fromDate, toDate, artProperties.getClassic());
        List<String> concertIds = getIdList(fromDate, toDate, artProperties.getConcert());
        concertIds.addAll(getIdList(fromDate, toDate, artProperties.getConcertMagic()));
    }

    // 2. Feign Clinet 이용 데이터 불러옴 -> Obkect -> Pojo 전환 -> Id 추출
    private List<String> getIdList(LocalDate fromDate, LocalDate toDate, String genreCode) {
        Object response = artClient.getArtInfoList(artProperties.getServiceKey(), fromDate, toDate, "1", "999", genreCode, "Y");
        ArtInfoResponse artInfoResponse = ob.convertValue(response, ArtInfoResponse.class);
        List<String> idList = artInfoResponse.getIdList().stream()
                .map(ArtInfoResponse.Id::getArtId)
                .collect(Collectors.toList());
        return idList;
    }
}
