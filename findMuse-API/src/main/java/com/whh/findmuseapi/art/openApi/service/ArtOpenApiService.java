package com.whh.findmuseapi.art.openApi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whh.findmuseapi.art.feign.ArtClient;
import com.whh.findmuseapi.art.openApi.ArtApiProperties;
import com.whh.findmuseapi.art.openApi.dto.ArtInfoDetailResponse;
import com.whh.findmuseapi.art.openApi.dto.ArtInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

import static com.whh.findmuseapi.art.openApi.dto.ArtInfoResponse.*;


@RequiredArgsConstructor
@EnableConfigurationProperties(ArtApiProperties.class)
@Slf4j
@Service
public class ArtOpenApiService {

    private final ArtClient artClient;
    private final ArtApiProperties artProperties;

    /**
     * 공연ID 목록 가져와서 List 저장
     * 이후 공연ID별로 상세 정보 가져오기
     */
    public void getOpenApiArtList() {
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = LocalDate.of(2024, 12, 31);

        List<Db> musicalIdList = getIdList(fromDate, toDate, artProperties.getMusical());
        List<Db> dramaIdList = getIdList(fromDate, toDate, artProperties.getDrama());
        List<Db> danceIdList = getIdList(fromDate, toDate, artProperties.getDance());
        danceIdList.addAll(getIdList(fromDate, toDate, artProperties.getDanceBasic()));
        List<Db> classicIdList = getIdList(fromDate, toDate, artProperties.getClassic());
        List<Db> concertIds = getIdList(fromDate, toDate, artProperties.getConcert());
        concertIds.addAll(getIdList(fromDate, toDate, artProperties.getConcertMagic()));

        // 업데이트 예정
        getArtDetail(musicalIdList);
        getArtDetail(dramaIdList);
        getArtDetail(danceIdList);
        getArtDetail(classicIdList);
        getArtDetail(concertIds);
    }

    /**
     * 공연ID로 상세정보 불러오기
     */
    private List<ArtInfoDetailResponse> getArtDetail(List<Db> artIdList) {
        return artIdList.stream()
                .map(db -> artClient.getArtInfoDetail(db.getId(), artProperties.getServiceKey(), "Y")).toList();
    }

    /**
     * 카테고리별로 ID목록 가져오기
     */
    private List<Db> getIdList(LocalDate fromDate, LocalDate toDate, String genreCode) {
        ArtInfoResponse idList = artClient.getArtInfoList(artProperties.getServiceKey(), fromDate, toDate, "1", "20", genreCode, "Y");
        return idList.getDbList();
    }
}
