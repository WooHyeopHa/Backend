package com.whh.findmuseapi.art.openApi.service;

import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.art.feign.ArtClient;
import com.whh.findmuseapi.art.openApi.ArtApiProperties;
import com.whh.findmuseapi.art.openApi.dto.ArtInfoDetailResponse;
import com.whh.findmuseapi.art.openApi.dto.ArtInfoResponse;
import com.whh.findmuseapi.art.openApi.dto.PlaceInfoDetailResponse;
import com.whh.findmuseapi.art.repository.ArtRepository;
import com.whh.findmuseapi.common.constant.Infos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

import static com.whh.findmuseapi.art.openApi.dto.ArtInfoResponse.*;
import static com.whh.findmuseapi.common.constant.Infos.ArtType.*;


@RequiredArgsConstructor
@EnableConfigurationProperties(ArtApiProperties.class)
@Slf4j
@Service
public class ArtOpenApiService {

    private final ArtClient artClient;
    private final ArtApiProperties artProperties;
    private final ArtRepository artRepository;
    private static final LocalDate FROM_DATE = LocalDate.now();
    private static final LocalDate TO_DATE = LocalDate.of(2024, 12, 31);

    /**
     * 카테고리, 타입 지정
     */
    public void init() {
        api2Entity(artProperties.getMusical(), MUSICAL_DRAMA);
        api2Entity(artProperties.getDrama(), MUSICAL_DRAMA);
        api2Entity(artProperties.getConcert(), CONCERT);
        api2Entity(artProperties.getConcertMagic(), CONCERT);
        api2Entity(artProperties.getDance(), DANCE_CLASSIC);
        api2Entity(artProperties.getDanceBasic(), DANCE_CLASSIC);
        api2Entity(artProperties.getClassic(), DANCE_CLASSIC);
    }

    private void api2Entity(String category, Infos.ArtType type) {
        // 1.ID리스트 불러오기
        List<Db> idList = getIdList(category);
        // 2. 상세정보 불러오기
        List<ArtInfoDetailResponse> artDetailList = getArtDetail(idList);
        // 3. 장소 상제 불러오기
        List<PlaceInfoDetailResponse> placeDetailList = getPlaceDetail(getPlaceId(artDetailList));
        // 4. to Entity
        saveArt(artDetailList, placeDetailList, type);
    }

    /**
     * 엔티티 저장
     */
    private void saveArt(List<ArtInfoDetailResponse> artDetailList, List<PlaceInfoDetailResponse> placeDetailList, Infos.ArtType type) {
        for (int i = 0; i < artDetailList.size(); i++) {
            Art newArt = artDetailList.get(i).toEntity(type);
            placeDetailList.get(i).toEntity(newArt);
            artRepository.save(newArt);
        }
    }

    /**
     * 장소ID List 추출
     */
    private List<String> getPlaceId(List<ArtInfoDetailResponse> detailList) {
        return detailList.stream()
                .map(dto -> dto.getDetail().getPlaceId())
                .toList();
    }

    /**
     * 장소ID로 상세정보 불러오기
     */
    public List<PlaceInfoDetailResponse> getPlaceDetail(List<String> placeIdList) {
        return placeIdList.stream()
                .map(id -> artClient.getPlaceInfoDetail(id, artProperties.getServiceKey(), "Y")).toList();
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
    private List<Db> getIdList(String genreCode) {
        ArtInfoResponse idList = artClient.getArtInfoList(artProperties.getServiceKey(), FROM_DATE, TO_DATE, "1", "999", genreCode, "Y");
        return idList.getDbList();
    }
}
